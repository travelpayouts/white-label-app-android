package appconfig

import com.google.gson.Gson
import com.google.gson.JsonObject
import org.gradle.api.GradleException
import org.gradle.api.Project
import java.security.MessageDigest

/**
 * Режим рекламы, вычисленный задачей parseConfig и записанный в
 * advertising.properties в корне проекта.
 *
 * Раньше настройка рекламы работала подстановкой текста: parseConfig искал в
 * app/build.gradle.kts строку implementation(libs.appodeal.core) и заменял её на
 * нужный набор зависимостей. Когда зависимости SDK вынесли в модуль travel-sdk,
 * строка исчезла, замена стала пустой операцией, и реклама перестала
 * подключаться — молча, без единой ошибки.
 *
 * Теперь parseConfig ничего не подставляет: он пишет режим в отдельный файл, а
 * список зависимостей живёт в app/build.gradle.kts под when. Ломаться нечему —
 * искать нечего.
 *
 * configHash защищает от второй половины той же проблемы: файл режима —
 * производная от config/app_config.json, и они могут разъехаться, если партнёр
 * поправил конфиг и не запустил parseConfig. Тогда сборка остановится с
 * понятным текстом вместо тихо неработающей рекламы.
 */
object AdvertisingMode {

    const val NONE = "none"
    const val APPODEAL = "appodeal"
    const val APPODEAL_ADMOB = "appodeal_admob"

    const val FILE_NAME = "advertising.properties"
    private const val KEY_MODE = "adsMode"
    private const val KEY_HASH = "configHash"
    private const val CONFIG_PATH = "config/app_config.json"

    /** Режим по содержимому конфига. Используется и при записи, и при сверке. */
    fun modeOf(appodealApiKey: String?, googleAdmobAppId: String?): String = when {
        appodealApiKey.isNullOrBlank() -> NONE
        googleAdmobAppId.isNullOrBlank() -> APPODEAL
        else -> APPODEAL_ADMOB
    }

    /**
     * Отпечаток блока advertising. Считается по канонической сериализации, а не
     * по сырому тексту, чтобы переформатирование конфига не считалось
     * изменением.
     */
    fun hashOf(advertising: JsonObject?): String {
        val canonical = Gson().toJson(advertising ?: JsonObject())
        val digest = MessageDigest.getInstance("SHA-256").digest(canonical.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun fileContent(mode: String, hash: String): String = """
        # Сгенерировано задачей parseConfig. Руками не редактировать.
        # Режим рекламы вычисляется из блока advertising в $CONFIG_PATH.
        $KEY_MODE=$mode
        $KEY_HASH=$hash
    """.trimIndent() + "\n"

    /**
     * Читает режим для сборки приложения и останавливает её, если состояние
     * несогласованно. Вызывается из app/build.gradle.kts.
     */
    fun read(project: Project): String {
        // parseConfig этот файл и создаёт, поэтому во время его запуска отсутствие
        // файла — нормальное состояние загрузки, а не ошибка. Иначе на чистом
        // клоне не запустить ни сборку, ни задачу, которая её чинит.
        val runningParseConfig = project.gradle.startParameter.taskNames
            .any { it.substringAfterLast(':').equals(GRADLE_TASK_NAME, ignoreCase = true) }

        val file = project.rootProject.file(FILE_NAME)
        if (!file.exists()) {
            if (runningParseConfig) return NONE
            throw GradleException(
                "$FILE_NAME не найден в корне проекта. Запустите ./gradlew $GRADLE_TASK_NAME"
            )
        }

        val props = file.readLines()
            .filterNot { it.isBlank() || it.trimStart().startsWith("#") }
            .mapNotNull { line -> line.split("=", limit = 2).takeIf { it.size == 2 } }
            .associate { it[0].trim() to it[1].trim() }

        val mode = props[KEY_MODE]
            ?: throw GradleException("В $FILE_NAME нет ключа $KEY_MODE. Запустите ./gradlew $GRADLE_TASK_NAME")

        if (mode !in listOf(NONE, APPODEAL, APPODEAL_ADMOB)) {
            throw GradleException("В $FILE_NAME неизвестный $KEY_MODE=$mode. Запустите ./gradlew $GRADLE_TASK_NAME")
        }

        // Сверка с конфигом: если партнёр правил advertising и не перезапустил
        // parseConfig, режим устарел и реклама соберётся не так, как он ожидает
        val configFile = project.rootProject.file(CONFIG_PATH)
        if (configFile.exists()) {
            val advertising = Gson()
                .fromJson(configFile.readText(), JsonObject::class.java)
                ?.getAsJsonObject("advertising")
            val actual = hashOf(advertising)
            val stored = props[KEY_HASH]
            if (!runningParseConfig && stored != null && stored != actual) {
                throw GradleException(
                    "Блок advertising в $CONFIG_PATH изменился после последнего parseConfig, " +
                        "поэтому $FILE_NAME устарел и реклама соберётся не так, как настроено. " +
                        "Запустите ./gradlew parseConfig"
                )
            }
        }

        return mode
    }
}

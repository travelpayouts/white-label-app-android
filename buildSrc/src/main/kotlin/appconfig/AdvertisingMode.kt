package appconfig

import com.google.gson.Gson
import com.google.gson.JsonElement
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
    private const val VERIFY_TASK_NAME = "verifyAdvertisingWiring"
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
     * Отпечаток блока advertising по канонической форме: ключи на всех уровнях
     * отсортированы, поэтому переформатирование конфига или перестановка полей
     * редактором не считаются изменением. Gson сам порядок не нормализует — он
     * сохраняет порядок вставки, поэтому сортируем до сериализации.
     */
    fun hashOf(advertising: JsonObject?): String {
        val canonical = canonicalize(advertising ?: JsonObject())
        val digest = MessageDigest.getInstance("SHA-256").digest(canonical.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }

    /** Стабильное текстовое представление: ключи отсортированы рекурсивно. */
    private fun canonicalize(element: JsonElement): String = when {
        element.isJsonObject -> element.asJsonObject.entrySet()
            .sortedBy { it.key }
            .joinToString(",", "{", "}") { "\"${it.key}\":${canonicalize(it.value)}" }
        element.isJsonArray -> element.asJsonArray
            .joinToString(",", "[", "]") { canonicalize(it) }
        else -> element.toString()
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
    /**
     * Явное переопределение режима через -PadsMode. Нужно, чтобы собрать ветку
     * с рекламой, не трогая отгружаемый config/app_config.json: в шаблоне
     * ключи рекламы обязаны быть пустыми, этого требует check-no-secrets.sh.
     *
     * Переопределение действует и на выбор зависимостей, и на проверку —
     * иначе параметр менял бы только ожидание проверки, а не то, что реально
     * собирается.
     */
    fun override(project: Project): String? {
        val value = project.providers.gradleProperty("adsMode").orNull ?: return null
        if (value !in listOf(NONE, APPODEAL, APPODEAL_ADMOB)) {
            throw GradleException(
                "Неизвестный -PadsMode=$value. Допустимые: $NONE, $APPODEAL, $APPODEAL_ADMOB"
            )
        }
        // Белый список вместо чёрного. Переопределение существует ровно для
        // задачи проверки, поэтому разрешаем его только когда запрошена именно
        // она. Перечислять запрещённое бесполезно: release прячется за
        // агрегатами (build, assemble, bundle), за сокращениями (aBRel) и за
        // типом Rc, который тоже минифицируется и подписывается релизным ключом.
        val requested = taskNames(project)
        val onlyVerification = requested.isNotEmpty() &&
            requested.all { matchesTask(it, VERIFY_TASK_NAME) }
        if (!onlyVerification) {
            throw GradleException(
                "-PadsMode=$value можно использовать только с задачей $VERIFY_TASK_NAME, " +
                    "и ни с какой другой. Это режим проверки: он подменяет режим рекламы, " +
                    "не трогая config/app_config.json, поэтому собранный с ним артефакт " +
                    "не соответствует конфигурации. Запрошено: ${requested.joinToString(" ")}"
            )
        }
        return value
    }

    /**
     * Просил ли пользователь задачу parseConfig. Gradle разрешает сокращать имя
     * задачи по заглавным буквам, поэтому точного сравнения мало: ./gradlew pC
     * запускает parseConfig, а в taskNames лежит буквально набранная строка.
     */
    /**
     * Имена задач, которые пользователь набрал. Аргументы опций сюда тоже
     * попадают (например `help --task parseConfig` даёт три элемента), поэтому
     * отбрасываем флаги и значение, идущее сразу за флагом.
     */
    private fun taskNames(project: Project): List<String> {
        val raw = project.gradle.startParameter.taskNames
        val result = mutableListOf<String>()
        var skipNext = false
        for (item in raw) {
            if (skipNext) { skipNext = false; continue }
            if (item.startsWith("-")) { skipNext = !item.contains("="); continue }
            result += item
        }
        return result
    }

    private fun isParseConfigRequested(project: Project): Boolean =
        taskNames(project).any { matchesTask(it, GRADLE_TASK_NAME) }

    /**
     * Совпадает ли набранное имя с задачей, с учётом сокращений Gradle по
     * заглавным буквам: ./gradlew pC запускает parseConfig.
     */
    private fun matchesTask(requested: String, taskName: String): Boolean {
        val name = requested.substringAfterLast(':')
        if (name.equals(taskName, ignoreCase = true)) return true
        if (name.isEmpty() || !taskName.startsWith(name.first(), ignoreCase = true)) return false
        var i = 0
        for (ch in name) {
            i = taskName.indexOf(ch, i, ignoreCase = true)
            if (i < 0) return false
            i++
        }
        return true
    }

    /**
     * parseConfig пишет файл на фазе выполнения, а зависимости выбираются на
     * фазе конфигурации — то есть РАНЬШЕ. В одной команде сборка успеет
     * прочитать старый режим. Тот же класс ошибки, что copyBasicSdk вместе с
     * assemble: молча уезжает не то, что ожидали.
     */
    private fun guardCombinedInvocation(project: Project) {
        val requested = taskNames(project)
        if (requested.any { matchesTask(it, GRADLE_TASK_NAME) } && requested.size > 1) {
            throw GradleException(
                "$GRADLE_TASK_NAME нельзя запускать в одной команде с другими задачами: " +
                    "режим рекламы выбирается до того, как задача успеет его записать, и " +
                    "сборка возьмёт старый. Запустите ./gradlew $GRADLE_TASK_NAME отдельно, " +
                    "затем остальное."
            )
        }
    }

    fun read(project: Project): String {
        // Запрет совмещать parseConfig со сборкой проверяем ДО переопределения:
        // иначе -PadsMode открывал бы обходной путь к той же ошибке
        guardCombinedInvocation(project)

        // Переопределение с командной строки имеет приоритет над файлом и
        // отключает сверку с конфигом: режим задан явно и намеренно
        override(project)?.let {
            project.logger.lifecycle(
                "ВНИМАНИЕ: режим рекламы переопределён параметром -PadsMode=$it, " +
                    "файл $FILE_NAME игнорируется. Это режим проверки, не для релизной сборки."
            )
            return it
        }

        // parseConfig этот файл и создаёт, поэтому во время его запуска отсутствие
        // файла — нормальное состояние загрузки, а не ошибка. Иначе на чистом
        // клоне не запустить ни сборку, ни задачу, которая её чинит.
        val parseConfigRequested = isParseConfigRequested(project)

        val file = project.rootProject.file(FILE_NAME)
        if (!file.exists()) {
            if (parseConfigRequested) return NONE
            throw GradleException(
                "$FILE_NAME не найден в корне проекта. Запустите ./gradlew $GRADLE_TASK_NAME"
            )
        }

        val props = file.readLines()
            .filterNot { it.isBlank() || it.trimStart().startsWith("#") }
            .mapNotNull { line -> line.split("=", limit = 2).takeIf { it.size == 2 } }
            .associate { it[0].trim() to it[1].trim() }

        val mode = props[KEY_MODE]
        if (mode == null || mode !in listOf(NONE, APPODEAL, APPODEAL_ADMOB)) {
            // Во время parseConfig испорченный файл — не тупик: задача его перезапишет.
            // Иначе совет «запустите parseConfig» вёл бы к той же ошибке.
            if (parseConfigRequested) return NONE
            throw GradleException(
                "В $FILE_NAME ${if (mode == null) "нет ключа $KEY_MODE" else "неизвестный $KEY_MODE=$mode"}. " +
                    "Запустите ./gradlew $GRADLE_TASK_NAME — задача перезапишет файл."
            )
        }

        // Сверка с конфигом: если партнёр правил advertising и не перезапустил
        // parseConfig, режим устарел и реклама соберётся не так, как он ожидает
        val configFile = project.rootProject.file(CONFIG_PATH)
        if (!configFile.exists()) {
            // Раньше сверка тут просто пропускалась, и сборка молча ехала по
            // файлу режима, который никто не мог подтвердить
            throw GradleException(
                "$CONFIG_PATH не найден, поэтому проверить актуальность $FILE_NAME не по чему. " +
                    "Восстановите конфигурацию: это источник всех настроек шаблона."
            )
        }
        run {
            val advertising = Gson()
                .fromJson(configFile.readText(), JsonObject::class.java)
                ?.getAsJsonObject("advertising")
            val actual = hashOf(advertising)
            val stored = props[KEY_HASH]
            if (!parseConfigRequested && stored != actual) {
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

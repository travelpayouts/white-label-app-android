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
        return value
    }

    /**
     * Просил ли пользователь задачу parseConfig. Gradle разрешает сокращать имя
     * задачи по заглавным буквам, поэтому точного сравнения мало: ./gradlew pC
     * запускает parseConfig, а в taskNames лежит буквально набранная строка.
     */
    /**
     * Проверки, которые нельзя делать по командной строке.
     *
     * Разбирать `startParameter.taskNames` самостоятельно бесполезно: там лежит
     * ровно то, что набрал пользователь, вместе с сокращениями и опциями задач.
     * Угадывать, какая опция забирает следующий аргумент, не выйдет — булева
     * `--rerun` аргумента не берёт, и попытка её пропустить съедала настоящее
     * имя задачи, открывая обход обеих защит.
     *
     * Поэтому спрашиваем сам Gradle: граф задач содержит то, что будет
     * выполнено, уже после разбора сокращений, опций и зависимостей.
     */
    /** Первые пять имён и счётчик: в графе бывает под четыреста задач. */
    private fun summarize(names: Set<String>): String {
        val head = names.sorted().take(5)
        val rest = names.size - head.size
        return head.joinToString(", ") + if (rest > 0) " и ещё $rest" else ""
    }

    fun registerGuards(project: Project) {
        val overridden = project.providers.gradleProperty("adsMode").orNull != null
        project.gradle.taskGraph.whenReady(
            object : org.gradle.api.Action<org.gradle.api.execution.TaskExecutionGraph> {
                override fun execute(graph: org.gradle.api.execution.TaskExecutionGraph) {
                    val names = graph.allTasks.map { it.name }.toSet()

                    if (overridden) {
                        val other = names - VERIFY_TASK_NAME
                        if (other.isNotEmpty()) {
                            throw GradleException(
                                "-PadsMode можно использовать только с задачей " +
                                    "$VERIFY_TASK_NAME. В графе задач есть ещё: " +
                                    summarize(other) + ". Это режим " +
                                    "проверки: он подменяет режим рекламы, не трогая " +
                                    "$CONFIG_PATH, поэтому собранный так артефакт не " +
                                    "соответствует конфигурации."
                            )
                        }
                    }

                    // Сгенерированные ресурсы устарели? Проверяем здесь, а не на
                    // конфигурации: только в графе видно, будет ли выполнен
                    // parseConfig. Прежняя догадка «упоминался ли он в команде»
                    // принимала любую подпоследовательность букв, и ./gradlew aR
                    // (штатное сокращение assembleRelease) отключал сверку.
                    if (GRADLE_TASK_NAME !in names) {
                        val configFile = project.rootProject.file(CONFIG_PATH)
                        if (configFile.exists()) {
                            val props = readProps(project)
                            val advertising = Gson()
                                .fromJson(configFile.readText(), JsonObject::class.java)
                                ?.getAsJsonObject("advertising")
                            if (props[KEY_HASH] != hashOf(advertising)) {
                                throw GradleException(
                                    "Блок advertising в $CONFIG_PATH изменился после последнего " +
                                        "$GRADLE_TASK_NAME, поэтому сгенерированные ресурсы рекламы " +
                                        "устарели: в манифесте и appodeal_config.xml остались прежние " +
                                        "значения. Запустите ./gradlew $GRADLE_TASK_NAME"
                                )
                            }
                        }
                    }

                    if (GRADLE_TASK_NAME in names && names.size > 1) {
                        throw GradleException(
                            "$GRADLE_TASK_NAME нельзя выполнять вместе с другими задачами: " +
                                "режим рекламы выбирается на конфигурации, до того как " +
                                "задача успеет его записать, поэтому сборка возьмёт старый. " +
                                "В графе: " + summarize(names - GRADLE_TASK_NAME) + ". Запустите ./gradlew " +
                                "$GRADLE_TASK_NAME отдельно, затем остальное."
                        )
                    }
                }
            }
        )
    }

    /**
     * Строковое поле конфига. Gson молча приводит число и boolean к строке, и
     * `"appodeal_api_key": 1` включал рекламу с заведомо неверным ключом — все
     * проверки при этом зелёные. Тип проверяем явно.
     */
    private fun stringField(obj: JsonObject?, name: String): String? {
        val el = obj?.get(name) ?: return null
        if (el.isJsonNull) return null
        if (!el.isJsonPrimitive || !el.asJsonPrimitive.isString) {
            throw GradleException(
                "В $CONFIG_PATH поле advertising.$name должно быть строкой, а не ${el}. " +
                    "Ключи рекламы задаются строками, в кавычках."
            )
        }
        return el.asString
    }

    private fun readProps(project: Project): Map<String, String> {
        val file = project.rootProject.file(FILE_NAME)
        if (!file.exists()) return emptyMap()
        return file.readLines()
            .filterNot { it.isBlank() || it.trimStart().startsWith("#") }
            .mapNotNull { line -> line.split("=", limit = 2).takeIf { it.size == 2 } }
            .associate { it[0].trim() to it[1].trim() }
    }

    fun read(project: Project): String {
        // Переопределение с командной строки имеет приоритет над конфигом.
        // Разрешено только для задачи проверки — это следит сторож на графе задач
        override(project)?.let {
            project.logger.lifecycle(
                "ВНИМАНИЕ: режим рекламы переопределён параметром -PadsMode=$it, " +
                    "конфигурация игнорируется. Это режим проверки, не для релизной сборки."
            )
            return it
        }

        // Конфиг — единственный источник режима, без него собирать нечего
        val configFile = project.rootProject.file(CONFIG_PATH)
        if (!configFile.exists()) {
            throw GradleException(
                "$CONFIG_PATH не найден. Это источник всех настроек шаблона: без него " +
                    "нельзя ни собрать приложение, ни запустить $GRADLE_TASK_NAME. " +
                    "Восстановите файл из поставки."
            )
        }

        // Режим ВЫВОДИТСЯ из конфига, а не читается из файла.
        //
        // Раньше он читался из advertising.properties, и это был второй источник
        // истины: configHash отвечал на вопрос «менялся ли конфиг после
        // parseConfig», но не на вопрос «соответствует ли режим этому конфигу».
        // Правки одной строки в файле хватало, чтобы получить 18 рекламных
        // адаптеров при пустых ключах — и verifyAdvertisingWiring отвечал «в
        // порядке», потому что читал режим оттуда же. То есть исходный дефект
        // воспроизводился редактированием файла в корне поставки.
        //
        // Теперь источник один — config/app_config.json. Файл остаётся только
        // признаком того, что сгенерированные ресурсы (манифест,
        // appodeal_config.xml) отвечают текущему конфигу.
        val advertising = Gson()
            .fromJson(configFile.readText(), JsonObject::class.java)
            ?.getAsJsonObject("advertising")
        val mode = modeOf(
            appodealApiKey = stringField(advertising, "appodeal_api_key"),
            googleAdmobAppId = stringField(advertising, "google_admob_app_id")
        )

        return mode
    }
}

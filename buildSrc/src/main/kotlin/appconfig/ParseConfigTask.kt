package appconfig

import appconfig.parsers.AppConfigJsonParser
import appconfig.parsers.AppDescriptionHandler
import appconfig.parsers.AppNameHandler
import appconfig.parsers.AppTabsParser
import appconfig.parsers.AppVersionHandler
import appconfig.parsers.BgImageParser
import appconfig.parsers.GoogleAdMobAppIdHandler
import appconfig.parsers.GoogleServicesHandler
import appconfig.parsers.HandlingLinkHandler
import appconfig.parsers.HsvColorsHandler
import appconfig.parsers.IconsHandler
import appconfig.parsers.LabColorsHandler
import appconfig.parsers.PartnerUrlHandler
import appconfig.parsers.PolicyUrlHandler
import appconfig.parsers.StringsHandler
import com.google.gson.Gson
import java.io.FileInputStream
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

abstract class ParseConfigTask : DefaultTask() {

    /**
     * Checks everything that would otherwise fail late, or silently produce a
     * wrong build. Runs before any parser touches the project: a partial
     * regeneration is harder to recover from than a refusal to start.
     *
     * Values are read as nullable on purpose. Gson builds the model without
     * calling Kotlin constructors, so a key missing from the JSON leaves a
     * declared non-null field null, and a plain check would throw a bare NPE —
     * exactly the opaque failure this function exists to replace.
     */
    private fun validateConfig(config: WhiteLabelConfiguration) {
        val android = config.baseConfiguration.identifier.android
        val applicationId: String? = android.applicationId

        if (applicationId.isNullOrBlank()) {
            throw GradleException(
                "identifier.android.id is empty in config/app_config.json. " +
                    "Set your application id, for example com.mycompany.travel."
            )
        }
        if (applicationId != applicationId.trim()) {
            throw GradleException(
                "identifier.android.id in config/app_config.json has leading or " +
                    "trailing spaces: '$applicationId'."
            )
        }
        // The generators substitute this value with a [0-9A-Za-z.] regex, so an
        // underscore or a dash would corrupt app_version.properties and the
        // generated Kotlin on the next run. Keep the accepted set in sync with
        // AppVersionHandler.
        val applicationIdFormat = Regex("[a-zA-Z][a-zA-Z0-9]*(\\.[a-zA-Z][a-zA-Z0-9]*)+")
        if (!applicationIdFormat.matches(applicationId)) {
            throw GradleException(
                "identifier.android.id in config/app_config.json is not usable as an " +
                    "application id here: '$applicationId'. Expected at least two " +
                    "segments separated by dots, each starting with a letter and " +
                    "containing only letters and digits, for example com.mycompany.travel."
            )
        }

        val versionName: String? = android.versionName
        if (versionName.isNullOrBlank()) {
            throw GradleException(
                "identifier.android.versionName is empty in config/app_config.json, " +
                    "for example \"1.0.0\"."
            )
        }
        if (android.versionCode <= 0) {
            throw GradleException(
                "identifier.android.versionCode in config/app_config.json must be a " +
                    "positive number — Google Play rejects 0."
            )
        }

        validateGoogleServices(applicationId)

        val constants = config.constants
        val marker: String? = constants.marker
        if (marker.isNullOrBlank() || marker == "0") {
            logger.warn(
                "WARNING: constants.marker is not set in config/app_config.json. " +
                    "The app will build, but no commission will be credited to you."
            )
        }
        if (constants.apiKey.isNullOrBlank()) {
            logger.warn(
                "WARNING: constants.api_key is not set in config/app_config.json. " +
                    "Flight search will not work."
            )
        }
        if (constants.clientDeviceHost.isNullOrBlank()) {
            logger.warn(
                "WARNING: constants.client_device_host is not set in " +
                    "config/app_config.json. The SDK will identify your app with an " +
                    "empty host in its requests."
            )
        }
    }

    /**
     * The Firebase config must list both the application id and its debug
     * variant, otherwise the build fails much later with an opaque
     * "No matching client found for package name".
     */
    private fun validateGoogleServices(applicationId: String) {
        val file = project.file(GoogleServicesHandler.GOOGLE_SERVICES_JSON_PATH)
        if (!file.exists()) {
            throw GradleException(
                "${GoogleServicesHandler.GOOGLE_SERVICES_JSON_PATH} not found. Put your " +
                    "own file from the Firebase Console there."
            )
        }

        val packageNames: List<String> = try {
            val root = Gson().fromJson(file.readText(), com.google.gson.JsonObject::class.java)
            val clients = root?.getAsJsonArray("client")
                ?: throw IllegalStateException("no \"client\" array")
            clients.mapNotNull { client ->
                client.asJsonObject
                    ?.getAsJsonObject("client_info")
                    ?.getAsJsonObject("android_client_info")
                    ?.get("package_name")
                    ?.takeIf { it.isJsonPrimitive }
                    ?.asString
            }
        } catch (e: Exception) {
            throw GradleException(
                "${GoogleServicesHandler.GOOGLE_SERVICES_JSON_PATH} is not a usable " +
                    "google-services.json (${e.message ?: e.javaClass.simpleName}). " +
                    "Download it again for your Android app from the Firebase Console."
            )
        }

        val missing = listOf(applicationId, "$applicationId.debug").filterNot { it in packageNames }
        if (missing.isNotEmpty()) {
            throw GradleException(
                "${GoogleServicesHandler.GOOGLE_SERVICES_JSON_PATH} has no client for " +
                    "${missing.joinToString(" and ")}. Add the app to your Firebase " +
                    "project with that package name (debug builds use the .debug suffix) " +
                    "and download the file again. Found: " +
                    packageNames.joinToString(", ").ifEmpty { "none" } + "."
            )
        }
    }

    companion object {
        const val BASE_COLOR_PROP_NAME = "baseColor"

        const val CONFIG_DIR = "config"
        const val JSON_FILE_NAME = "app_config.json"
        const val OUTPUT_FILE_NAME = "AppConfig.kt"
        const val OUTPUT_FILE_PATH = "/src/main/kotlin/com/travelapp/config/"
        const val CUSTOM_MODULE_NAME = "config"
    }

    @TaskAction
    fun parseConfig() {
        // Задача обращается к project на этапе выполнения, а configuration cache
        // это запрещает. Без явной проверки партнёр попадает в тупик: сборка
        // требует запустить parseConfig, а parseConfig падает с «App module not
        // found!», где про кэш нет ни слова.
        if (project.gradle.startParameter.isConfigurationCacheRequested) {
            throw GradleException(
                "$GRADLE_TASK_NAME несовместим с configuration cache. Запустите " +
                    "./gradlew $GRADLE_TASK_NAME --no-configuration-cache, а остальные " +
                    "команды можно оставить с кэшем: обычная сборка с ним работает."
            )
        }


        val appModule =
            project.childProjects["app"] ?: throw IllegalStateException("App module not found!")

        val fis = FileInputStream(project.file("$CONFIG_DIR/$JSON_FILE_NAME"))

        val jsonString = fis
            .bufferedReader()
            .use { it.readText() }


        val buildSrcAppConfig = Gson().fromJson(jsonString, WhiteLabelConfiguration::class.java)

        validateConfig(buildSrcAppConfig)

        // region Parsers

        AppConfigJsonParser.parse(buildSrcAppConfig, appModule)

        val advertisingSnapshot = com.google.gson.Gson()
            .fromJson(project.rootProject.file("config/app_config.json").readText(), com.google.gson.JsonObject::class.java)
            ?.getAsJsonObject("advertising")

        GoogleAdMobAppIdHandler.handleAdmobConfig(
            project = project,
            appModule = appModule,
            advertising = advertisingSnapshot,
            googleAdmobAppId = buildSrcAppConfig.advertising?.googleAdmobAppId?.trim().orEmpty(),
            isAppodealKeyEmpty = buildSrcAppConfig.advertising?.appodealApiKey.isNullOrBlank()
        )

        AppTabsParser.parseTabs(appModule, buildSrcAppConfig.whiteLabelConfig.screensToDisplay)

        GoogleServicesHandler.copyGoogleServicesJson(project)

        AppVersionHandler.generate(
            project,
            buildSrcAppConfig.baseConfiguration.identifier.android.applicationId,
            buildSrcAppConfig.baseConfiguration.identifier.android.versionName,
            buildSrcAppConfig.baseConfiguration.identifier.android.versionCode
        )

        HandlingLinkHandler.generate(
            project,
            buildSrcAppConfig.constants.sharingData?.handlingLink?.replace("https://", "").orEmpty()
        )

        AppNameHandler.generateAppNameStrings(
            appModule,
            buildSrcAppConfig.baseConfiguration.displayName
        )

        AppDescriptionHandler.generateAppDescriptionStrings(
            appModule,
            buildSrcAppConfig.infoScreenConfig.aboutAppInfo.description
        )

        PartnerUrlHandler.generatePartnerUrlStrings(
            appModule,
            buildSrcAppConfig.infoScreenConfig.aboutAppInfo.partnerUrl
        )


        PolicyUrlHandler.generatePolicyUrlXml(appModule, buildSrcAppConfig.constants.policyUrl)

        val baseColor: String = if (project.hasProperty(BASE_COLOR_PROP_NAME)) {
            project.property(BASE_COLOR_PROP_NAME) as String
        } else {
            buildSrcAppConfig.style.baseColor
        }

        val palette = buildSrcAppConfig.style.palette


        if (palette == "hsv") {
            HsvColorsHandler.generateColorsXml(
                project = appModule,
                baseColorString = baseColor,
                overriddenColors = buildSrcAppConfig.style.overriddenColor
            )
        } else {
            LabColorsHandler.generateColorsXml(
                project = appModule,
                baseColorString = baseColor,
                overriddenColors = buildSrcAppConfig.style.overriddenColor
            )
        }

        IconsHandler.copyCustomAppIcons(project, appModule)

        IconsHandler.copyCustomOtherIcons(project, appModule)

        BgImageParser.parseImage(project, appModule)

        StringsHandler.copyStringsFiles(project, appModule)

        // Файл режима рекламы пишем последним, когда вся генерация уже прошла.
        // Иначе авария в середине задачи оставляет свежий отпечаток конфига при
        // старых сгенерированных файлах — ровно то состояние, которое отпечаток
        // и должен ловить: следующая сборка молча сочтёт всё согласованным.
        // Отпечаток считаем по ТОМУ ЖЕ тексту, из которого сгенерированы ресурсы.
        // Раньше файл читался заново, и правка конфига во время работы задачи
        // (она идёт около семи секунд) давала ресурсы по одному снимку и хеш по
        // другому — следующая сборка считала состояние согласованным.
        GoogleAdMobAppIdHandler.writeAdvertisingProperties(
            project = project,
            advertising = advertisingSnapshot,
            googleAdmobAppId = buildSrcAppConfig.advertising?.googleAdmobAppId?.trim().orEmpty(),
            isAppodealKeyEmpty = buildSrcAppConfig.advertising?.appodealApiKey.isNullOrBlank()
        )

        //TODO
        //OtherTabInfoParser.parseTabs(appModule, buildSrcAppConfig.whiteLabelConfig)

        // endregion

        // DEPRECATED

        /*val customChildProject = this.project.childProjects[CUSTOM_MODULE_NAME]
        if (customChildProject != null) {

            // AppConfig
            val path =
                "${customChildProject.layout.projectDirectory}$OUTPUT_FILE_PATH$OUTPUT_FILE_NAME"
            val outputFileProvider = customChildProject.layout.projectDirectory.file(path)
            val outputFile = outputFileProvider.asFile

            outputFile.writeText(
                AppConfigClassTemplate(buildSrcAppConfig).generateAppConfigClass()
            )
*/


    }
}



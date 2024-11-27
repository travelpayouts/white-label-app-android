package appconfig

import appconfig.parsers.AppConfigJsonParser
import appconfig.parsers.AppDescriptionHandler
import appconfig.parsers.AppNameHandler
import appconfig.parsers.AppTabsParser
import appconfig.parsers.AppVersionHandler
import appconfig.parsers.BgImageParser
import appconfig.parsers.GoogleAdMobAppIdHandler
import appconfig.parsers.GoogleMapsApiKeyHandler
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
import org.gradle.api.tasks.TaskAction

abstract class ParseConfigTask : DefaultTask() {

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

        val appModule =
            project.childProjects["app"] ?: throw IllegalStateException("App module not found!")

        val fis = FileInputStream("$CONFIG_DIR/$JSON_FILE_NAME")

        val jsonString = fis
            .bufferedReader()
            .use { it.readText() }


        val buildSrcAppConfig = Gson().fromJson(jsonString, WhiteLabelConfiguration::class.java)

        // region Parsers

        AppConfigJsonParser.parse(buildSrcAppConfig, appModule)

        GoogleAdMobAppIdHandler.handleAdmobConfig(
            appModule = appModule,
            googleAdmobAppId = buildSrcAppConfig.baseConfiguration.advertising?.googleAdmobAppId.orEmpty(),
            isAppodealKeyEmpty = buildSrcAppConfig.baseConfiguration.advertising?.appodealApiKey.isNullOrBlank()
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

        GoogleMapsApiKeyHandler.generateXml(appModule, buildSrcAppConfig.constants.googleMapsApiKey)

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



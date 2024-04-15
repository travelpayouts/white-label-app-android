package appconfig

import com.google.gson.Gson
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.FileInputStream
import appconfig.parsers.*
import java.lang.IllegalStateException

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

        /*        appModule.extensions.findByType(AppExtension::class.java)!!.sourceSets.getByName("main").java.srcDir(
                    File(appModule.getGeneratedSourcesDir().toString())
                )

                appModule.extensions.findByType(AppExtension::class.java)!!.sourceSets.getByName("main").java.srcDirs.forEach {
                    println("srcDir: $it")
                }

                appModule.extensions.findByType(AppExtension::class.java)!!.applicationVariants.all {

                }*/

        val fis = FileInputStream("$CONFIG_DIR/$JSON_FILE_NAME")

        val jsonString = fis
            .bufferedReader()
            .use { it.readText() }


        val buildSrcAppConfig = Gson().fromJson(jsonString, WhiteLabelConfiguration::class.java)

        // region Parsers

        AppConfigJsonParser.parse(buildSrcAppConfig, appModule)

        GoogleAdMobAppIdHandler.generateXml(appModule, buildSrcAppConfig.baseConfiguration.advertising?.googleAdmobAppId.orEmpty())

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
            HsvColorsHandler.generateColorsXml(appModule, baseColor)
        } else {
            LabColorsHandler.generateColorsXml(appModule, baseColor)
        }

        IconsHandler.copyCustomIcons(project, appModule)

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



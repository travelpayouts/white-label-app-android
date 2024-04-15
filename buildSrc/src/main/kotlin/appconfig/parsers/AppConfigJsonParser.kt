package appconfig.parsers

import appconfig.WhiteLabelConfiguration
import appconfig.templates.AppConfigClassTemplate
import org.gradle.api.Project

object AppConfigJsonParser {

    private const val packageNamePath = "/com/travelapp/config"

    private const val srcDir = "src/main/kotlin"

    private const val filename = "WhiteLabelConfiguration.kt"

    /**
     * Creates WhiteLabelConfig.kt from 'app_config.json'
     */
    fun parse(buildSrcAppConfig: WhiteLabelConfiguration, appModule: Project) {

        val file = appModule.layout.projectDirectory.file("${srcDir}${packageNamePath}/${filename}").asFile
        file.parentFile.mkdirs()

        file.writeText(
            AppConfigClassTemplate(buildSrcAppConfig).generateAppConfigClass()
        )
    }

}
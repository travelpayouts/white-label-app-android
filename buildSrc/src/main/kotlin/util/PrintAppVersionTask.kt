package util

import configuration.ApplicationVersions
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Gradle task to print app version for setting TeamCity build number
 *
 * @author Anatolii Shulipov (as@cleverpumpkin.ru)
 */
abstract class PrintAppVersionTask : DefaultTask() {

    companion object {
        const val SHOW_BUILD_NUMBER_PARAM_KEY = "showBuildNumber"

        const val CUSTOM_CONFIG_NAME = "customConfigName"
    }

    private val shouldShowBuildNumber = if (project.hasProperty(SHOW_BUILD_NUMBER_PARAM_KEY)) {
        project.properties[SHOW_BUILD_NUMBER_PARAM_KEY].toString().toBoolean()
    } else {
        false
    }

    private val configName = if (project.hasProperty(CUSTOM_CONFIG_NAME)) {
        project.properties[CUSTOM_CONFIG_NAME].toString()
    } else {
        "defaultConfig"
    }

    @TaskAction
    fun printAppVersion() {
        val version = if (shouldShowBuildNumber) {
            ApplicationVersions.FULL_APP_VERSION_NAME
        } else {
            ApplicationVersions.APP_VERSION_NAME
        }

        val versionWithConfigName = if (configName.isNotBlank()) {
            "$version-$configName"
        } else {
            version
        }
        println(
            "##teamcity[buildNumber '$versionWithConfigName']"
        )
        println(
            "##teamcity[setParameter name='env.CP_PRODUCT_VERSION_FULL' value='$versionWithConfigName']"
        )
    }

}

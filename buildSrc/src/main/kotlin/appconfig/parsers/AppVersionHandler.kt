package appconfig.parsers

import org.gradle.api.Project

object AppVersionHandler {

    private const val FILE_NAME = "app_version.properties"
    private const val START_ACTIVITY_FILE_NAME = "app/src/main/kotlin/whitelabel/StartActivity.kt"
    private const val APPLICATION_ID = "applicationId="
    private const val VERSION = "version="
    private const val VERSION_CODE = "versionCode="
    private const val PACKAGE = "package "


    fun generate(
        customProject: Project,
        applicationId: String,
        versionName: String,
        versionCode: Int,
    ) {
        print("Generating app_version.properties... ")

        val file = customProject.file(FILE_NAME)

        if (!file.exists()) {
            file.createNewFile()
        }

        val text = file.readText()

        val regexId = "applicationId=[0-9A-Za-z.]*".toRegex()
        val regexVersion = "version=[0-9A-Za-z.]*".toRegex()
        val regexVersionCode = "versionCode=[0-9A-Za-z.]*".toRegex()
        val editedText = text
            .replace(regexId, APPLICATION_ID + applicationId)
            .replace(regexVersion, VERSION + versionName)
            .replace(regexVersionCode, VERSION_CODE + versionCode.toString())

        file.writeText(editedText)

        editAppId(customProject, applicationId)

        println("✅ ")
    }

    private fun editAppId(
        customProject: Project,
        applicationId: String,
    ) {
        val file = customProject.file(START_ACTIVITY_FILE_NAME)
        val regex = "package [0-9A-Za-z.]*".toRegex()
        val text = file.readText()
        val editedText = text
            .replace(regex, PACKAGE + applicationId)

        file.writeText(editedText)
    }
}
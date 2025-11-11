package appconfig.parsers

import org.gradle.api.Project

object AppVersionHandler {

    private const val FILE_NAME = "app_version.properties"
    private const val START_ACTIVITY_FILE_NAME = "app/src/main/kotlin/whitelabel/StartActivity.kt"
    private const val TRAVEL_MESSAGING_SERVICE_FILE_NAME = "app/src/main/kotlin/com/travelapp/TravelAppMessagingService.kt"
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
        var file = customProject.file(START_ACTIVITY_FILE_NAME)
        var regex = "package [0-9A-Za-z.]*".toRegex()
        var text = file.readText()
        var editedText = text
            .replace(regex, PACKAGE + applicationId)

        file.writeText(editedText)

        file = customProject.file(TRAVEL_MESSAGING_SERVICE_FILE_NAME)
        regex = "import [0-9A-Za-z.]*.StartActivity".toRegex()
        text = file.readText()
        editedText = text
            .replace(regex, "import $applicationId.StartActivity")

        file.writeText(editedText)
    }
}
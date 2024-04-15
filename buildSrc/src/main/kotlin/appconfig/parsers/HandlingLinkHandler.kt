package appconfig.parsers

import org.gradle.api.Project

object HandlingLinkHandler {

    private const val FILE_NAME = "handling_link.properties"

    fun generate(
        customProject: Project,
        handlingLink: String
    ) {
        print("Generating handling_link.properties... ")

        val file = customProject.file(FILE_NAME)

        if (!file.exists()) {
            file.createNewFile()
        }

        val editedText = "handlingLink=$handlingLink"

        file.writeText(editedText)

        println("✅ ")
    }
}
package appconfig.parsers

import appconfig.Localization
import org.gradle.api.Project

object AppNameHandler {

    private const val STRINGS_FILENAME = "app_name.xml"
    private const val STRING_RES_NAME = "ta_app_name"

    /**
     * Create resource file with app name string resource
     */
    fun generateAppNameStrings(customProject: Project, localization: Localization) {
        print("Generating app name strings.xml... ")

        LocalizationParser.parseLocalization(customProject, STRINGS_FILENAME, STRING_RES_NAME, localization)

        println("✅ ")
    }

}
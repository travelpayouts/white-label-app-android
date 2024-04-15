package appconfig.parsers

import appconfig.Localization
import org.gradle.api.Project

object AppDescriptionHandler {

    private const val STRINGS_FILENAME = "app_description.xml"
    private const val STRING_RES_NAME = "ta_app_description"

    /**
     * Create resource file with app description string resource
     */
    fun generateAppDescriptionStrings(customProject: Project, localization: Localization) {
        print("Generating app description strings.xml... ")

        LocalizationParser.parseLocalization(customProject, STRINGS_FILENAME, STRING_RES_NAME, localization)

        println("✅ ")
    }

}
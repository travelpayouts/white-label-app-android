package appconfig.parsers

import appconfig.Localization
import org.gradle.api.Project

object PartnerUrlHandler {

    private const val STRINGS_FILENAME = "partner_url.xml"
    private const val STRING_RES_NAME = "ta_partner_url"

    /**
     * Create resource file with partner url string resource
     */
    fun generatePartnerUrlStrings(customProject: Project, localization: Localization) {
        print("Generating partner url strings.xml... ")

        LocalizationParser.parseLocalization(customProject, STRINGS_FILENAME, STRING_RES_NAME, localization)

        println("✅ ")
    }

}
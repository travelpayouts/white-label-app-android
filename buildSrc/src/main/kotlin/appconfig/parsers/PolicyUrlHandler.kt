package appconfig.parsers

import appconfig.Localization
import org.gradle.api.Project

object PolicyUrlHandler {

    private const val FILENAME = "policy_url.xml"
    private const val STRING_RES_NAME = "ta_policy_url"

    /**
     * Create string resource files (with provided localizations) with policy url string
     */
    fun generatePolicyUrlXml(project: Project, localization: Localization) {
        print("Generating 'policy_url.xml'...   ")

        LocalizationParser.parseLocalization(project, FILENAME, STRING_RES_NAME, localization)

        println("✅ ")
    }

}


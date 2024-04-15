package appconfig.parsers

import appconfig.WhiteLabelConfig
import appconfig.WhiteLabelConfiguration
import org.gradle.api.Project

object OtherTabInfoParser {

    private const val RES_PATH = "src/main/res/"
    private const val NAVIGATION_RES_PATH = "${RES_PATH}navigation/"
    private const val VALUES_RES_PATH = "${RES_PATH}values/"
    private const val TEMPLATE_FILE = "${NAVIGATION_RES_PATH}ta_other.xml"

    private val OTHER_IDS_FILE_TEMPLATE = """
        <?xml version="1.0" encoding="utf-8"?>
            <resources>
                <item name="%s" type="id"/>
        </resources>
    """.trimIndent()

    fun parseTabs(
        project: Project,
        whiteLabelConfig: WhiteLabelConfig
    ) {
        print("Parsing 'other' tabs resources...    ")

        whiteLabelConfig.screensToDisplay.forEach { tab ->
            if (tab.type == "other" && tab.parameters != null) {
                LocalizationParser.parseLocalization(
                    project = project,
                    filename = "ta_title_${tab.parameters.id.lowercase()}.xml",
                    parameterName = "ta_title_${tab.parameters.id.lowercase()}",
                    localization = tab.parameters.title
                )

                LocalizationParser.parseLocalization(
                    project = project,
                    filename = "ta_url_${tab.parameters.id.lowercase()}.xml",
                    parameterName = "ta_url_${tab.parameters.id.lowercase()}",
                    localization = tab.parameters.url
                )

                val templateGraphFile = project.file(TEMPLATE_FILE)
                val targetFile = project.file("${NAVIGATION_RES_PATH}${tab.parameters.id.lowercase()}.xml")
                templateGraphFile.copyTo(targetFile, true)

                val idsFile = project.file("$VALUES_RES_PATH${tab.parameters.id.lowercase()}.xml")
                idsFile.writeText(OTHER_IDS_FILE_TEMPLATE.format(tab.parameters.id.lowercase()))
            }
        }

        println("✅ ")
    }

}
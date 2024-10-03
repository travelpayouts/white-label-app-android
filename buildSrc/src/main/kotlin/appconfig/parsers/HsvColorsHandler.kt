package appconfig.parsers

import appconfig.modifyHSB
import org.gradle.api.Project
import java.awt.Color
import appconfig.templates.colorsValueXmlTemplate
import appconfig.templates.makeColorXmlString
import appconfig.templates.primaryDarkKey
import appconfig.templates.primaryDisableDarkKey
import appconfig.templates.primaryDisableLightKey
import appconfig.templates.primaryLightKey
import appconfig.templates.primaryPressedDarkKey
import appconfig.templates.primaryPressedLightKey
import appconfig.templates.surfaceTintDarkKey
import appconfig.templates.surfaceTintLightKey
import appconfig.toHexString


object HsvColorsHandler {

    private const val FILENAME = "colors.xml"

    private const val PATH = "/src/main/res/values/"


    fun generateColorsXml(
        project: Project,
        baseColorString: String,
        overriddenColors: Map<String, String>
    ) {
        print("Generating colors.xml... ")
        val baseColor = Color.decode(baseColorString)

        val currentColors = overriddenColors.toMutableMap()

        val primaryLight = baseColor.toHexString()
        currentColors.putIfAbsent(primaryLightKey, primaryLight)

        val primaryDark = baseColor.modifyHSB(0.55f, 1f).toHexString()
        currentColors.putIfAbsent(primaryDarkKey, primaryDark)

        val primaryDisableLight = baseColor.modifyHSB(0.5f, 1.0f).toHexString()
        currentColors.putIfAbsent(primaryDisableLightKey, primaryDisableLight)

        val primaryDisableDark = baseColor.modifyHSB(0.5f, 0.3f).toHexString()
        currentColors.putIfAbsent(primaryDisableDarkKey, primaryDisableDark)

        val primaryPressedLight = baseColor.modifyHSB(1f, 0.7f).toHexString()
        currentColors.putIfAbsent(primaryPressedLightKey, primaryPressedLight)

        val primaryPressedDark = baseColor.modifyHSB(0.8f, 0.7f).toHexString()
        currentColors.putIfAbsent(primaryPressedDarkKey, primaryPressedDark)

        val surfaceTintLight = baseColor.modifyHSB(0.6f, 1f, 0.2f).toHexString()
        currentColors.putIfAbsent(surfaceTintLightKey, surfaceTintLight)

        val surfaceTintDark = baseColor.modifyHSB(0.3f, 0.3f, 0.2f).toHexString()
        currentColors.putIfAbsent(surfaceTintDarkKey, surfaceTintDark)

        val path = project.layout.projectDirectory.toString() + PATH + FILENAME
        val outputFileProvider = project.layout.projectDirectory.file(path)
        val outputFile = outputFileProvider.asFile

        if (!outputFile.exists()) {
            outputFile.createNewFile()
        }

        var colorsResourcesText = ""
        currentColors.forEach { (key, value) ->
            val newColorString = makeColorXmlString(colorName = key, colorHex = value)
            colorsResourcesText += newColorString
        }

        outputFile.writeText(colorsValueXmlTemplate.format(colorsResourcesText))

        println("✅ ")
    }

}
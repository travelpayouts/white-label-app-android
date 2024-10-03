package appconfig.parsers

import appconfig.adjustLightness
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
import appconfig.withAlpha
import com.github.ajalt.colormath.RenderCondition
import com.github.ajalt.colormath.model.RGB
import org.gradle.api.Project
import org.jetbrains.kotlin.util.prefixIfNot

object LabColorsHandler {

    private const val FILENAME = "colors.xml"

    private const val PATH = "/src/main/res/values/"

    /**
     *  Generate resource file with color pallete based on provided color
     */
    fun generateColorsXml(
        project: Project,
        baseColorString: String,
        overriddenColors: Map<String, String>
    ) {
        print("Generating colors.xml... ")
        val baseColor = RGB(baseColorString)

        val currentColors = overriddenColors.toMutableMap()

        val primaryLight = baseColor.adjustLightness(50f).toHex(true)
        currentColors.putIfAbsent(primaryLightKey, primaryLight)

        val primaryDark = baseColor.adjustLightness(70f).toHex(true)
        currentColors.putIfAbsent(primaryDarkKey, primaryDark)

        val primaryDisableLight = baseColor.adjustLightness(50f).withAlpha(0.3f).toHexString()
        currentColors.putIfAbsent(primaryDisableLightKey, primaryDisableLight)

        val primaryDisableDark = baseColor.adjustLightness(70f).withAlpha(0.3f).toHexString()
        currentColors.putIfAbsent(primaryDisableDarkKey, primaryDisableDark)

        val primaryPressedLight = baseColor.adjustLightness(30f).toHex(true)
        currentColors.putIfAbsent(primaryPressedLightKey, primaryPressedLight)

        val primaryPressedDark = baseColor.adjustLightness(50f).toHex(true)
        currentColors.putIfAbsent(primaryPressedDarkKey, primaryPressedDark)


        // add '#29' which is 16% alpha. (.toHex() formats in rgba, but we need argb)
        val surfaceTintLight =
            baseColor.adjustLightness(50f).toHex(false, RenderCondition.NEVER).prefixIfNot("#29")
        currentColors.putIfAbsent(surfaceTintLightKey, surfaceTintLight)

        val surfaceTintDark =
            baseColor.adjustLightness(70f).toHex(false, RenderCondition.NEVER).prefixIfNot("#29")
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
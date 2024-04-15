package appconfig.parsers

import appconfig.modifyHSB
import org.gradle.api.Project
import java.awt.Color
import appconfig.templates.colorsXmlTemplate
import appconfig.toHexString
import appconfig.withAlpha
import appconfig.adjustLightness
import com.github.ajalt.colormath.RenderCondition
import com.github.ajalt.colormath.model.LABColorSpace
import com.github.ajalt.colormath.model.LABColorSpaces
import com.github.ajalt.colormath.model.RGB
import org.jetbrains.kotlin.util.prefixIfNot

object LabColorsHandler {

    private const val FILENAME = "colors.xml"

    private const val PATH = "/src/main/res/values/"

    /**
     *  Generate resource file with color pallete based on provided color
     */
    fun generateColorsXml(project: Project, baseColorString: String) {
        print("Generating colors.xml... ")
        val baseColor = RGB(baseColorString)

        val primaryLight = baseColor.adjustLightness(50f).toHex(true)
        val primaryDark = baseColor.adjustLightness(70f).toHex(true)

        val primaryDisableLight = baseColor.adjustLightness(50f).withAlpha(0.3f).toHexString()
        val primaryDisableDark = baseColor.adjustLightness(70f).withAlpha(0.3f).toHexString()

        val primaryPressedLight = baseColor.adjustLightness(30f).toHex(true)
        val primaryPressedDark = baseColor.adjustLightness(50f).toHex(true)


        // add '#29' which is 16% alpha. (.toHex() formats in rgba, but we need argb)
        val surfaceTintLight =
            baseColor.adjustLightness(50f).toHex(false, RenderCondition.NEVER).prefixIfNot("#29")
        val surfaceTintDark =
            baseColor.adjustLightness(70f).toHex(false, RenderCondition.NEVER).prefixIfNot("#29")

        val path = project.layout.projectDirectory.toString() + PATH + FILENAME
        val outputFileProvider = project.layout.projectDirectory.file(path)
        val outputFile = outputFileProvider.asFile

        if (!outputFile.exists()) {
            outputFile.createNewFile()
        }

        outputFile.writeText(
            colorsXmlTemplate.format(
                primaryLight,
                primaryDark,
                primaryDisableLight,
                primaryDisableDark,
                primaryPressedLight,
                primaryPressedDark,
                surfaceTintLight,
                surfaceTintDark
            )
        )

        println("✅ ")
    }

}
package appconfig.parsers

import appconfig.modifyHSB
import org.gradle.api.Project
import java.awt.Color
import appconfig.templates.colorsXmlTemplate
import appconfig.toHexString


object HsvColorsHandler {

    private const val FILENAME = "colors.xml"

    private const val PATH = "/src/main/res/values/"


    fun generateColorsXml(project: Project, baseColorString: String) {
        print("Generating colors.xml... ")
        val baseColor = Color.decode(baseColorString)
        val primaryLight = baseColor.toHexString()
        val primaryDark = baseColor.modifyHSB(0.55f, 1f).toHexString() //TODO discuss with designer

        val primaryDisableLight = baseColor.modifyHSB(0.5f, 1.0f).toHexString()
        val primaryDisableDark = baseColor.modifyHSB(0.5f, 0.3f).toHexString()

        val primaryPressedLight = baseColor.modifyHSB(1f, 0.7f).toHexString()
        val primaryPressedDark = baseColor.modifyHSB(0.8f, 0.7f).toHexString()

        //val onPrimaryDisableLight = baseColor.modifyHSB(0.3f, 0.8f).toHexString()
        //val onPrimaryDisableDark = baseColor.modifyHSB(0.1f, 0.8f).toHexString()

        val surfaceTintLight = baseColor.modifyHSB(0.6f, 1f, 0.2f).toHexString()
        val surfaceTintDark = baseColor.modifyHSB(0.3f, 0.3f, 0.2f).toHexString()

        println("surface tint: $surfaceTintLight")

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
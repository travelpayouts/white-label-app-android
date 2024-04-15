package appconfig

import com.github.ajalt.colormath.RenderCondition
import com.github.ajalt.colormath.model.RGB
import org.gradle.api.Project
import org.jetbrains.kotlin.util.prefixIfNot
import java.awt.Color
import java.io.File

const val GRADLE_TASK_NAME = "parseConfig"

fun Project.getGeneratedSourcesDir(): String {
    return "${buildDir}/generated/source/travelapp"
}


fun Color.modifyHSB(s: Float, b: Float): Color {
    val hsb = Color.RGBtoHSB(red, green, blue, null)
    return Color.getHSBColor(hsb[0], hsb[1] * s, hsb[2] * b)
}

fun Color.modifyHSB(s: Float, b: Float, a: Float): Color {
    val hsb = Color.RGBtoHSB(red, green, blue, null)
    val hsbColor = Color.getHSBColor(hsb[0], hsb[1] * s, hsb[2] * b)
    return Color(hsbColor.red, hsbColor.green, hsbColor.blue, (a * 255).toInt())
}

fun RGB.adjustLightness(lightness: Float): RGB {
    return this.toLAB().copy(l = lightness).toSRGB()
}

fun RGB.withAlpha(alpha: Float): RGB {
    return this.copy(alpha = alpha)
}

fun RGB.toHexString(): String {
    val alphaHex = "#%02X".format(alphaInt)
    return this.toHex(false, RenderCondition.NEVER).prefixIfNot(alphaHex)
}

fun Color.toHexString(): String {
    println("alpha: ${String.format("%02X", alpha)}")
    val color = String.format("#%02X%02X%02X%02X", alpha, red, green, blue)
    println("Color: $color")
    return color
}



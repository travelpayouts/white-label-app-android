package appconfig.templates

import appconfig.GRADLE_TASK_NAME

val primaryLightKey = "ta_primary_light"
val primaryDarkKey = "ta_primary_dark"
val primaryDisableLightKey = "ta_primary_disable_light"
val primaryDisableDarkKey = "ta_primary_disable_dark"
val primaryPressedLightKey = "ta_primary_pressed_light"
val primaryPressedDarkKey = "ta_primary_pressed_dark"
val surfaceTintLightKey = "ta_surface_tint_light"
val surfaceTintDarkKey = "ta_surface_tint_dark"

fun makeColorXmlString(colorName: String, colorHex: String): String {
    return "        <color name=\"$colorName\">$colorHex</color>\n"
}

val colorsValueXmlTemplate = """
<?xml version="1.0" encoding="utf-8"?>
<!--DO NOT MODIFY THIS FILE! IT IS AUTOGENERATED BY '$GRADLE_TASK_NAME' GRADLE TASK-->
<resources>
%s
</resources>
""".trimIndent()
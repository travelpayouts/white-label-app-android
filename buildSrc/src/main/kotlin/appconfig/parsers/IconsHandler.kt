package appconfig.parsers

import org.gradle.api.Project

object IconsHandler {

    private const val IC_FOREGROUND_ICON_FILENAME = "ic_launcher_foreground"
    private const val IC_BACKGROUND_ICON_FILENAME = "ic_launcher_background"

    private const val IC_OTHER_1_FILENAME = "ic_other_1"
    private const val IC_OTHER_2_FILENAME = "ic_other_2"

    private const val TA_IC_FOREGROUND_ICON_FILENAME = "ta_ic_launcher_foreground"
    private const val TA_IC_BACKGROUND_ICON_FILENAME = "ta_ic_launcher_background"

    private const val TA_IC_OTHER_1_FILENAME = "ta_ic_other_1"
    private const val TA_IC_OTHER_2_FILENAME = "ta_ic_other_2"

    private const val EXT_XML = ".xml"
    const val EXT_PNG = ".png"

    private const val DRAWABLES_DIR = "src/main/res/drawable/"

    private const val ICONS_DIR = "config/icons/"

    /**
     * Copy icons from 'icons' dir
     */
    fun copyCustomAppIcons(project: Project, appModule: Project) {
        print("Copying custom app icons...      ")

        val srcForegroundIconFile = project.layout.projectDirectory.file(ICONS_DIR + IC_FOREGROUND_ICON_FILENAME + EXT_XML).asFile
        val srcBackgroundIconFile = project.layout.projectDirectory.file(ICONS_DIR + IC_BACKGROUND_ICON_FILENAME + EXT_XML).asFile

        if (srcBackgroundIconFile.exists() && srcForegroundIconFile.exists()) {

            val dstForegroundIconFile = appModule.layout.projectDirectory.file(DRAWABLES_DIR + TA_IC_FOREGROUND_ICON_FILENAME + EXT_XML).asFile
            val dstBackgroundIconFile = appModule.layout.projectDirectory.file(DRAWABLES_DIR + TA_IC_BACKGROUND_ICON_FILENAME + EXT_XML).asFile

            srcForegroundIconFile.copyTo(dstForegroundIconFile, true, 1024)
            srcBackgroundIconFile.copyTo(dstBackgroundIconFile, true, 1024)

            println("✅ ")
        } else {
            println("No icons found. Skipping")
        }
    }

    fun copyCustomOtherIcons(project: Project, appModule: Project) {
        println("Copying custom other icons...      ")

        val srcOther1File = project.layout.projectDirectory.file(ICONS_DIR + IC_OTHER_1_FILENAME + EXT_XML).asFile
        val srcOther2File = project.layout.projectDirectory.file(ICONS_DIR + IC_OTHER_2_FILENAME + EXT_XML).asFile

        val dstOther1File = appModule.layout.projectDirectory.file(DRAWABLES_DIR + TA_IC_OTHER_1_FILENAME + EXT_XML).asFile
        val dstOther2File = appModule.layout.projectDirectory.file(DRAWABLES_DIR + TA_IC_OTHER_2_FILENAME + EXT_XML).asFile

        if (srcOther1File.exists()) {
            print("Copying custom other_1 icons...      ")

            srcOther1File.copyTo(dstOther1File, true, 1024)

            println("✅ ")
        } else {
            if (dstOther1File.exists()) {
                dstOther1File.delete()
            }

            println("No other_1 icon found. Skipping")
        }

        if (srcOther2File.exists()) {
            print("Copying custom other_2 icons...      ")

            srcOther2File.copyTo(dstOther2File, true, 1024)

            println("✅ ")
        } else {
            if (dstOther2File.exists()) {
                dstOther2File.delete()
            }

            println("No other_2 icon found. Skipping")
        }
    }

}
package appconfig.parsers

import org.gradle.api.Project

object IconsHandler {

    private const val IC_FOREGROUND_ICON_FILENAME = "ic_launcher_foreground"
    private const val IC_BACKGROUND_ICON_FILENAME = "ic_launcher_background"

    private const val EXT_XML = ".xml"
    const val EXT_PNG = ".png"

    private const val DRAWABLES_DIR = "src/main/res/drawable/"

    private const val ICONS_DIR = "config/icons/"

    /**
     * Copy icons from 'icons' dir
     */
    fun copyCustomIcons(project: Project, appModule: Project) {
        print("Copying custom icons...      ")

        val srcForegroundIconFile = project.layout.projectDirectory.file(ICONS_DIR + IC_FOREGROUND_ICON_FILENAME + EXT_XML).asFile
        val srcBackgroundIconFile = project.layout.projectDirectory.file(ICONS_DIR + IC_BACKGROUND_ICON_FILENAME + EXT_XML).asFile

        if (srcBackgroundIconFile.exists() && srcForegroundIconFile.exists()) {

            val dstForegroundIconFile = appModule.layout.projectDirectory.file(DRAWABLES_DIR + IC_FOREGROUND_ICON_FILENAME + EXT_XML).asFile
            val dstBackgroundIconFile = appModule.layout.projectDirectory.file(DRAWABLES_DIR + IC_BACKGROUND_ICON_FILENAME + EXT_XML).asFile

            srcForegroundIconFile.copyTo(dstForegroundIconFile, true, 1024)
            srcBackgroundIconFile.copyTo(dstBackgroundIconFile, true, 1024)

            println("✅ ")
        } else {
            println("No icons found. Skipping")
        }
    }

}
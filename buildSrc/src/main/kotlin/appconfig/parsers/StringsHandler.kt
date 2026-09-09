package appconfig.parsers

import org.gradle.api.Project
import java.io.File
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name

object StringsHandler {

    private const val stringsDirPath = "config/strings/"
    private const val targetPath = "src/main/res/values"
    private const val fileName = "strings.xml"

    fun copyStringsFiles(project: Project, configModule: Project) {
        print("Copying 'strings.xml'...   ")

        val stringsDir = project.layout.projectDirectory.file(stringsDirPath)

        // The directory is optional: a partner happy with the bundled translations
        // may not have one. Without this check the task failed here with a bare
        // NoSuchFileException, having already rewritten half the project.
        if (!stringsDir.asFile.exists()) {
            println("no $stringsDirPath, skipping")
            return
        }

        val dirs = stringsDir.asFile.toPath().listDirectoryEntries()
        dirs
            .filter { !it.name.startsWith(".") } // Filter hidden dirs
            .forEach {
            val dir = it.name
            val file = File("$it/$fileName")

            val targetPath = if (dir == "base") {
                "$targetPath/$fileName"
            } else {
                "$targetPath-$dir/$fileName"
            }
            val target = configModule.layout.projectDirectory.file(targetPath).asFile

            file.copyTo(target, true)
        }

        println("✅ ")
    }

}
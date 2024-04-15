package util

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * @author Anatolii Shulipov (as@cleverpumpkin.ru)
 */
abstract class CleanBuildSrc : DefaultTask() {

    @TaskAction
    fun cleanBuildSrc() {
        println("Cleaning 'build' under 'buildSrc'")
        val buildSrcDir = File("${project.rootDir}/buildSrc/build")
        val success = buildSrcDir.deleteRecursively()
        if (success) {
            println("Success!")
        } else {
            println("Failed :-(")
        }
    }

}
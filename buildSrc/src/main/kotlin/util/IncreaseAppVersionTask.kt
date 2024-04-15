package util

import configuration.ApplicationVersions
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

/**
 * @author Anatolii Shulipov (as@cleverpumpkin.ru)
 */
abstract class IncreaseAppVersionTask : DefaultTask() {

    private val storeComment = "This is config file for app version \n" +
            "print app version to console for TeamCity using ./gradlew printAppVersion task\n" +
            "increment build version number using ./gradlew increaseAppVersion task\n"

    @TaskAction
    fun increaseAppVersion() {
        val fis = FileInputStream(ApplicationVersions.FILENAME)
        val props = Properties().apply {
            load(fis)
        }

        val buildNumberProp: Int? = props
            .getProperty(ApplicationVersions.PROP_BUILD_NUMBER)
            .toIntOrNull()

        val buildNumber = (buildNumberProp?.plus(1) ?: 1).toString()
            .padStart(2, '0')

        props.setProperty(ApplicationVersions.PROP_BUILD_NUMBER, buildNumber.toString())
        val out = FileOutputStream(ApplicationVersions.FILENAME)
        props.store(out, String.format(storeComment, buildNumber))

        fis.close()
        out.close()
    }


}
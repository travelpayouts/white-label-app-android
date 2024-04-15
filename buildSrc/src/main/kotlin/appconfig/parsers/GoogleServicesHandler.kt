package appconfig.parsers

import org.gradle.api.GradleException
import org.gradle.api.Project

object GoogleServicesHandler {

    private const val GOOGLE_SERVICES_JSON_PATH = "config/google-services.json"

    private const val TARGET_PATH = "src/release/google-services.json"


    private const val QA_TARGET_PATH = "src/qa/google-services.json"

    /**
     * Copy 'google-services.json' to project
     */
    fun copyGoogleServicesJson(project: Project) {
        print("Copying 'google-services.json'...    ")
        val appModule = project.childProjects["app"] ?: throw GradleException("app module not found!")

        val srcFile = project.file(GOOGLE_SERVICES_JSON_PATH)

        val dstFile = appModule.file(TARGET_PATH)
        val dstQaFile = appModule.file(QA_TARGET_PATH)

        srcFile.copyTo(dstFile, true, 1024)
        srcFile.copyTo(dstQaFile, true, 1024)
        println("✅ ")
    }

}
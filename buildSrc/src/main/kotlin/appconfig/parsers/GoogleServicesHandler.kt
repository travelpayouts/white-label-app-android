package appconfig.parsers

import org.gradle.api.GradleException
import org.gradle.api.Project

object GoogleServicesHandler {

    const val GOOGLE_SERVICES_JSON_PATH = "config/google-services.json"

    /**
     * Every build variant that keeps its own copy of google-services.json.
     * All of them must be refreshed: leaving a stale copy in any one variant
     * makes that variant fail with "No matching client found for package name".
     */
    private val TARGET_PATHS = listOf(
        "src/release/google-services.json",
        "src/qa/google-services.json",
        "src/debug/google-services.json",
        "src/rc/google-services.json",
    )

    /**
     * Copy 'google-services.json' to project
     */
    fun copyGoogleServicesJson(project: Project) {
        print("Copying 'google-services.json'...    ")
        val appModule = project.childProjects["app"] ?: throw GradleException("app module not found!")

        // ParseConfigTask checks that the file exists and holds the right clients
        // before any parser runs, so this only copies it.
        val srcFile = project.file(GOOGLE_SERVICES_JSON_PATH)

        TARGET_PATHS.forEach { path ->
            srcFile.copyTo(appModule.file(path), true, 1024)
        }
        println("✅ ")
    }

}
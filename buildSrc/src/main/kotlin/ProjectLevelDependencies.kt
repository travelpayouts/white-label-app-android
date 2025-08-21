import GradlePluginsVersions.FIREBASE_CRASHLYTICS_PLUGIN
import GradlePluginsVersions.FIREBASE_PERF_PLUGIN
import GradlePluginsVersions.GOOGLE_SERVICES_PLUGIN
import GradlePluginsVersions.GRADLE_PLUGIN
import GradlePluginsVersions.KOTLIN_PLUGIN
import GradlePluginsVersions.NAV_VERSION

internal object GradlePluginsVersions {
    const val GRADLE_PLUGIN = "8.2.2"
    const val KOTLIN_PLUGIN = "1.8.20"
    const val FIREBASE_PERF_PLUGIN = "1.4.2"
    const val FIREBASE_CRASHLYTICS_PLUGIN = "2.9.4"
    const val GOOGLE_SERVICES_PLUGIN = "4.4.3"
    const val NAV_VERSION = "2.5.3"
}

object GradlePlugins {
    const val GRADLE = "com.android.tools.build:gradle:$GRADLE_PLUGIN"
    const val KOTLIN = "org.jetbrains.kotlin:kotlin-gradle-plugin:$KOTLIN_PLUGIN"
    const val KOTLIN_SERIALIZATION = "org.jetbrains.kotlin:kotlin-serialization:$KOTLIN_PLUGIN"
    const val FIREBASE_PERF = "com.google.firebase:perf-plugin:$FIREBASE_PERF_PLUGIN"
    const val FIREBASE_CRASHLYTICS = "com.google.firebase:firebase-crashlytics-gradle:$FIREBASE_CRASHLYTICS_PLUGIN"
    const val GOOGLE_SERVICES = "com.google.gms:google-services:$GOOGLE_SERVICES_PLUGIN"
    const val NAV_SAFE_ARGS = "androidx.navigation:navigation-safe-args-gradle-plugin:$NAV_VERSION"
}

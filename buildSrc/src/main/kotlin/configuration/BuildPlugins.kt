package configuration

/**
 * Configuration of all gradle build plugins
 */
object BuildPlugins {
    const val ANDROID_APPLICATION = "com.android.application"
    const val ANDROID_LIBRARY = "com.android.library"

    const val KOTLIN_ANDROID = "kotlin-android"
    const val KOTLIN_KAPT = "kotlin-kapt"
    const val KOTLIN_PARCELIZE = "kotlin-parcelize"
    const val KOTLIN_SERIALIZATION = "plugin.serialization"

    const val NAVIGATION_SAFE_ARGS = "androidx.navigation.safeargs.kotlin"

    const val FIREBASE_PERF = "com.google.firebase.firebase-perf"
    const val FIREBASE_CRASHLYTICS = "com.google.firebase.crashlytics"
    const val GOOGLE_SERVICES = "com.google.gms.google-services"

    const val EASY_LAUNCHER = "com.starter.easylauncher"
}
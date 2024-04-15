plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
}

object PluginsVersions {
    const val GRADLE_PLUGIN = "8.2.2"
    const val KOTLIN_PLUGIN = "1.9.20"
    const val FIREBASE_PERF_PLUGIN = "1.4.2"
    const val FIREBASE_CRASHLYTICS_PLUGIN = "2.9.9"
    const val GOOGLE_SERVICES_PLUGIN = "4.4.0"
    const val NAV_VERSION = "2.7.5"
    const val KTOR_VERSION = "2.3.4"
}

dependencies {
    implementation("com.android.tools.build:gradle:${PluginsVersions.GRADLE_PLUGIN}")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${PluginsVersions.KOTLIN_PLUGIN}")
    implementation("org.jetbrains.kotlin:kotlin-serialization:${PluginsVersions.KOTLIN_PLUGIN}")
    implementation("com.google.firebase:perf-plugin:${PluginsVersions.FIREBASE_PERF_PLUGIN}")
    implementation("com.google.firebase:firebase-crashlytics-gradle:${PluginsVersions.FIREBASE_CRASHLYTICS_PLUGIN}")
    implementation("com.google.gms:google-services:${PluginsVersions.GOOGLE_SERVICES_PLUGIN}")
    implementation("androidx.navigation:navigation-safe-args-gradle-plugin:${PluginsVersions.NAV_VERSION}")


    
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.android.tools:sdk-common:31.2.0")
    implementation("com.github.ajalt.colormath:colormath:3.2.0")
}
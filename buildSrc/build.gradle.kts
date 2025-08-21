plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenLocal()
    mavenCentral()
}

object PluginsVersions {
    const val GRADLE_PLUGIN = "8.3.0"
    const val KOTLIN_PLUGIN = "1.9.23"
    const val FIREBASE_PERF_PLUGIN = "1.4.2"
    const val FIREBASE_CRASHLYTICS_PLUGIN = "2.9.9"
    const val GOOGLE_SERVICES_PLUGIN = "4.4.1"
    const val NAV_VERSION = "2.7.7"
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
//    implementation("com.travelapp.gradle:colorsgenerator:1.0.0")

    
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.android.tools:sdk-common:31.3.2")
    implementation("com.github.ajalt.colormath:colormath:3.2.0")
}
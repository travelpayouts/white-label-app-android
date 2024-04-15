

// Top-level build file where you can add configuration options common to all sub-projects/modules.
allprojects {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io")
        // Add Appodeal repository
        maven("https://artifactory.appodeal.com/appodeal")
    }
}

buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath(Google.GMS_SERVICES)
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}

tasks.register<util.PrintAppVersionTask>("printAppVersion")

tasks.register<util.IncreaseAppVersionTask>("increaseAppVersion")

tasks.register<util.CleanBuildSrc>("cleanBuildSrc")

tasks.register<appconfig.ParseConfigTask>("parseConfig")




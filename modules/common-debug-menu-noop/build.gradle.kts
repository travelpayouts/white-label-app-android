import buildconfig.LibraryModuleBuildConfiguration
import configuration.Config

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    namespace = "com.travelapp.common.debug.menu"
    LibraryModuleBuildConfiguration(project, libraryExtension = this).configure()

    kotlin {
        jvmToolchain(11)
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(11))
        }
    }
}

dependencies {
    coreLibraryDesugaring(Tools.DESUGAR_JDK_LIBS)
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(AndroidX.FRAGMENT)

}

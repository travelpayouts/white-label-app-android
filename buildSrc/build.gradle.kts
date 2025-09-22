plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(libs.gradle)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.kotlin.serialization)
    implementation(libs.perf.plugin)
    implementation(libs.firebase.crashlytics.gradle)
    implementation(libs.gms.services)
    implementation(libs.androidx.navigation.safe.args.gradle.plugin)
//    implementation("com.travelapp.gradle:colorsgenerator:1.0.0")


    implementation(libs.gson)
    implementation(libs.sdk.common)
    implementation(libs.colormath)
}
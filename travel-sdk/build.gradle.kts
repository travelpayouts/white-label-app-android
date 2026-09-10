// Wrapper module around the prebuilt travel-sdk-release.aar.
//
// Unlike a Maven-published library, an .aar file carries no list of its own
// dependencies — so they are declared here. Everything in the "default"
// configuration is exported to the app that consumes this module, so you do
// not need to repeat any of it in your own build file.
//
// Versions match SDK 1.7.2. Replace this file together with the .aar.
//
// This file is the source of truth for the Appodeal core version. The ad network
// adapters live in gradle/libs.versions.toml and have to match it: on the 3.x line
// Appodeal versions adapters together with the core, so core 3.12.0 goes with
// adapters 3.12.0.0. When you replace this file, bump `appodeal` in the catalog in
// the same change - that is the version the adapters actually use, and a mismatch
// does not fail the build, it silently stops the adapters from registering.
configurations.maybeCreate("default")
artifacts.add("default", file("travel-sdk-release.aar"))

dependencies {
    add("default", "androidx.databinding:viewbinding:9.0.0")
    add("default", "org.jetbrains.kotlin:kotlin-stdlib:2.2.21")
    add("default", "org.jetbrains.kotlin:kotlin-parcelize-runtime:2.2.10")
    add("default", "androidx.core:core-ktx:1.17.0")
    add("default", "androidx.appcompat:appcompat:1.7.1")
    add("default", "androidx.fragment:fragment-ktx:1.8.9")
    add("default", "androidx.constraintlayout:constraintlayout:2.2.1")
    add("default", "androidx.recyclerview:recyclerview:1.4.0")
    add("default", "com.google.android.material:material:1.13.0")
    add("default", "androidx.lifecycle:lifecycle-process:2.10.0")
    add("default", "androidx.lifecycle:lifecycle-runtime-ktx:2.10.0")
    add("default", "androidx.navigation:navigation-ui-ktx:2.9.7")
    add("default", "androidx.navigation:navigation-fragment-ktx:2.9.7")
    add("default", "com.github.kirich1409:viewbindingpropertydelegate-noreflection:1.5.9")
    add("default", "com.airbnb.android:lottie:6.7.1")
    add("default", "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    // Dagger-generated multibindings reference Guava collections
    add("default", "com.google.guava:guava:31.1-android")

    add("default", platform("com.google.firebase:firebase-bom:34.9.0"))
    add("default", "com.google.firebase:firebase-analytics")
    add("default", "com.google.firebase:firebase-crashlytics")
    add("default", platform("com.squareup.okhttp3:okhttp-bom:5.3.2"))
    add("default", "com.squareup.okhttp3:okhttp")
    add("default", "com.squareup.okhttp3:logging-interceptor")
    add("default", "com.squareup.retrofit2:retrofit:3.0.0")
    add("default", "com.squareup.retrofit2:converter-gson:3.0.0")
    add("default", "com.squareup.retrofit2:converter-scalars:3.0.0")
    add("default", "com.faltenreich:skeletonlayout:6.0.0")
    add("default", "com.google.code.gson:gson:2.13.2")
    add("default", "com.github.ajalt:timberkt:1.5.1")
    add("default", "dev.chrisbanes.insetter:insetter:0.6.1")
    add("default", "ru.cleverpumpkin:crunchycalendar:2.6.1")
    add("default", "com.appodeal.ads.sdk:core:3.12.0")
    add("default", "com.hannesdorfmann:adapterdelegates4:4.3.2")
    add("default", "com.hannesdorfmann:adapterdelegates4-kotlin-dsl:4.3.2")
    add("default", "com.hannesdorfmann:adapterdelegates4-kotlin-dsl-layoutcontainer:4.3.2")
    add("default", "com.hannesdorfmann:adapterdelegates4-kotlin-dsl-viewbinding:4.3.2")
    add("default", "io.github.reactivecircus.flowbinding:flowbinding-core:1.2.0")
    add("default", "io.github.reactivecircus.flowbinding:flowbinding-material:1.2.0")
    add("default", "io.github.reactivecircus.flowbinding:flowbinding-android:1.2.0")
    add("default", "io.github.reactivecircus.flowbinding:flowbinding-appcompat:1.2.0")
    add("default", "io.github.reactivecircus.flowbinding:flowbinding-recyclerview:1.2.0")
    add("default", "com.google.dagger:dagger:2.59.1")
    add("default", "io.coil-kt:coil-base:2.7.0")
    add("default", "io.coil-kt:coil:2.7.0")
    add("default", "com.appsflyer:af-android-sdk:6.17.5")
    add("default", "com.google.android.play:review:2.0.2")
    add("default", "com.google.android.play:review-ktx:2.0.2")
}

/**
 * Created by Ruslan Arslanov on 23/12/2019.
 */

object KotlinLibs {
    private const val COROUTINES_VERSION = "1.6.4"

    const val COROUTINES_CORE = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$COROUTINES_VERSION"
    const val COROUTINES_ANDROID = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$COROUTINES_VERSION"
}

object KotlinXSerialization {
    private const val VERSION = "1.5.0"
    const val LIBRARY = "org.jetbrains.kotlinx:kotlinx-serialization-json:$VERSION"
    const val PROPERTIES = "org.jetbrains.kotlinx:kotlinx-serialization-properties:$VERSION"

    const val RETROFIT_FACTORY = "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0"
}

object AndroidX {
    const val CORE = "androidx.core:core-ktx:1.12.0"
    const val APPCOMPAT = "androidx.appcompat:appcompat:1.6.1"
    const val FRAGMENT = "androidx.fragment:fragment-ktx:1.5.5"
    const val ANNOTATIONS = "androidx.annotation:annotation:1.6.0"
    const val PREFERENCE = "androidx.preference:preference-ktx:1.2.0"
    const val WORK_MANAGER = "androidx.work:work-runtime-ktx:2.8.0"
    const val RECYCLER_VIEW = "androidx.recyclerview:recyclerview:1.3.0"
    const val WEBKIT = "androidx.webkit:webkit:1.6.0"
    const val CONSTRAINT_LAYOUT = "androidx.constraintlayout:constraintlayout:2.1.4"
    const val PAGING = "androidx.paging:paging-runtime-ktx:3.1.1"
    const val CHROME_CUSTOM_TABS = "androidx.browser:browser:1.5.0"
}

object GooglePlay {
    const val CORE_KTX = "com.google.android.play:core-ktx:1.8.1"
}

object Google {
    const val GMS_SERVICES = "com.google.gms:google-services:4.3.15"
    const val MAPS_SERVICES = "com.google.maps:google-maps-services:2.2.0"
    const val MAPS_UTILS = "com.google.maps.android:android-maps-utils:2.3.0"
    const val MAPS_UTILS_KTX = "com.google.maps.android:maps-utils-ktx:3.4.0"
    const val PLAY_SERVICES_MAPS = "com.google.android.gms:play-services-maps:18.1.0"
    const val PLAY_SERVICES_LOCATION = "com.google.android.gms:play-services-location:21.0.1"
}

object Hms {
    const val CORE = "com.huawei.agconnect:agconnect-core:1.3.1.300"
    const val PUSH = "com.huawei.hms:push:5.1.1.301"
}

// decrease from 1.8.0 to 1.7.0 for removing bold text on selection in BottomNavigationView
object MaterialComponents {
    const val LIBRARY = "com.google.android.material:material:1.10.0"
}

object Dagger2 {
    private const val VERSION = "2.48"

    const val ANNOTATIONS = "javax.annotation:javax.annotation-api:1.3.2"

    const val LIBRARY = "com.google.dagger:dagger:$VERSION"
    const val COMPILER = "com.google.dagger:dagger-compiler:$VERSION"
}

object Retrofit {
    private const val VERSION = "2.10.0"

    const val LIBRARY = "com.squareup.retrofit2:retrofit:$VERSION"
    const val GSON_CONVERTER = "com.squareup.retrofit2:converter-gson:$VERSION"
    const val SCALAR_CONVERTER = "com.squareup.retrofit2:converter-scalars:$VERSION"
}

object OkHttp {
    private const val VERSION = "4.10.0"

    const val BOM = "com.squareup.okhttp3:okhttp-bom:$VERSION"
    const val LIBRARY = "com.squareup.okhttp3:okhttp"
    const val LOGGING_INTERCEPTOR = "com.squareup.okhttp3:logging-interceptor"
}

object Room {
    private const val VERSION = "2.5.0"

    const val RUNTIME = "androidx.room:room-runtime:$VERSION"
    const val COMPILER = "androidx.room:room-compiler:$VERSION"
    const val EXTENSIONS = "androidx.room:room-ktx:$VERSION"
    const val RX_EXTENSIONS = "androidx.room:room-rxjava2:$VERSION"
    const val SAFE_EXTENSIONS = "com.commonsware.cwac:saferoom.x:1.0.2"
}

object LifeCycle {
    private const val VERSION = "2.6.0"

    const val VIEW_MODEL_LIBRARY = "androidx.lifecycle:lifecycle-viewmodel-ktx:$VERSION"
    const val LIVEDATA_LIBRARY = "androidx.lifecycle:lifecycle-livedata-ktx:$VERSION"
    const val VIEW_MODEL_COMPILER = "androidx.lifecycle:lifecycle-compiler:$VERSION"
    const val LIFECYCLE_EXTENSIONS = "androidx.lifecycle:lifecycle-extensions:2.2.0"
    const val RUNTIME_EXTENSIONS = "androidx.lifecycle:lifecycle-runtime-ktx:$VERSION"
    const val COMMON = "androidx.lifecycle:lifecycle-common-java8:$VERSION"
}

object Navigation {
    private const val NAVIGATION_VERSION = "2.5.3"

    const val UI = "androidx.navigation:navigation-ui-ktx:$NAVIGATION_VERSION"
    const val FRAGMENT = "androidx.navigation:navigation-fragment-ktx:$NAVIGATION_VERSION"
}

object PlayServices {
    const val GCM = "com.google.android.gms:play-services-gcm:17.0.0"
    const val AUTH = "com.google.android.gms:play-services-auth:20.4.1"
    const val MAPS = "com.google.android.gms:play-services-maps:18.1.0"
    const val LOCATION = "com.google.android.gms:play-services-location:21.0.1"
}

object Firebase {
    const val BOM = "com.google.firebase:firebase-bom:31.2.3"
    const val CORE = "com.google.firebase:firebase-core"
    const val CRASHLYTICS = "com.google.firebase:firebase-crashlytics"
    const val ANALYTICS = "com.google.firebase:firebase-analytics-ktx"
    const val CLOUD_MESSAGING = "com.google.firebase:firebase-messaging-ktx"
    const val PERF = "com.google.firebase:firebase-perf-ktx"
}

object FlowBinding {
    private const val VERSION = "1.2.0"

    const val CORE = "io.github.reactivecircus.flowbinding:flowbinding-core:$VERSION"
    const val PLATFORM = "io.github.reactivecircus.flowbinding:flowbinding-android:$VERSION"
    const val ACTIVITY = "io.github.reactivecircus.flowbinding:flowbinding-activity:$VERSION"
    const val APPCOMPAT = "io.github.reactivecircus.flowbinding:flowbinding-appcompat:$VERSION"
    const val MATERIAL = "io.github.reactivecircus.flowbinding:flowbinding-material:$VERSION"
    const val NAVIGATION = "io.github.reactivecircus.flowbinding:flowbinding-navigation:$VERSION"
    const val VIEWPAGER = "io.github.reactivecircus.flowbinding:flowbinding-viewpager:$VERSION"
    const val VIEWPAGER2 = "io.github.reactivecircus.flowbinding:flowbinding-viewpager2:$VERSION"
    const val RECYCLERVIEW = "io.github.reactivecircus.flowbinding:flowbinding-recyclerview:$VERSION"
    const val SWIPE_REFRESH = "io.github.reactivecircus.flowbinding:flowbinding-swiperefreshlayout:$VERSION"

}

object Pluto {
    private const val VERSION = "2.0.9"

    const val LIBRARY = "com.plutolib:pluto:$VERSION"
    const val LIBRARY_NOOP = "com.plutolib:pluto-no-op:$VERSION"

    const val PLUGIN_NETWORK = "com.plutolib.plugins:network:$VERSION"
    const val PLUGIN_NETWORK_NOOP = "com.plutolib.plugins:network-no-op:$VERSION"

    const val PLUGIN_LOGGER = "com.plutolib.plugins:logger:$VERSION"
    const val PLUGIN_LOGGER_NOOP = "com.plutolib.plugins:logger-no-op:$VERSION"
}

object AdapterDelegate {
    private const val VERSION = "4.3.2"

    const val CORE = "com.hannesdorfmann:adapterdelegates4:$VERSION"
    const val LAYOUT_CONTAINER = "com.hannesdorfmann:adapterdelegates4-kotlin-dsl-layoutcontainer:$VERSION"
    const val DSL = "com.hannesdorfmann:adapterdelegates4-kotlin-dsl:$VERSION"
    const val VIEW_BINDING = "com.hannesdorfmann:adapterdelegates4-kotlin-dsl-viewbinding:$VERSION"
    const val PAGINATION = "com.hannesdorfmann:adapterdelegates4-pagination:$VERSION"
}

object Coil {
    private const val VERSION = "2.2.2"

    const val LIBRARY = "io.coil-kt:coil:$VERSION"
    const val BASE = "io.coil-kt:coil-base:$VERSION"
    const val GIF = "io.coil-kt:coil-gif:$VERSION"
    const val SVG = "io.coil-kt:coil-svg:$VERSION"
}

object UI {
    const val VIEWBINDING_PROPERTY_DELEGATE = "com.github.kirich1409:viewbindingpropertydelegate-noreflection:1.5.9"
}

object Skeleton {
    private const val VERSION = "4.0.0"

    const val LIBRARY = "com.faltenreich:skeletonlayout:$VERSION"
}

/**
 * Utilities and tools.
 */
object Tools {
    const val DESUGAR_JDK_LIBS = "com.android.tools:desugar_jdk_libs:2.0.2"
    const val JUNIT = "junit:junit:4.12"
    const val GSON = "com.google.code.gson:gson:2.10.1"
    const val TIMBER = "com.github.ajalt:timberkt:1.5.1"
    const val INSETTER = "dev.chrisbanes.insetter:insetter:0.6.1"
    const val BROWSER_HELPER = "com.google.androidbrowserhelper:androidbrowserhelper:2.4.0"
    const val KEYBOARD_VISIBILITY_EVENT = "net.yslibrary.keyboardvisibilityevent:keyboardvisibilityevent:3.0.0-RC3"
    const val CRUNCHY_CALENDAR = "ru.cleverpumpkin:crunchycalendar:2.6.1"
    const val SEISMIC = "com.squareup:seismic:1.0.3"
    const val APPODEAL = "com.appodeal.ads:sdk:3.1.3.0"
}

object Lottie {
    private const val LOTTIE_VERSION = "6.0.0"
    const val LIBRARY = "com.airbnb.android:lottie:$LOTTIE_VERSION"
}

object AppsFlyer {
    const val LIBRARY = "com.appsflyer:af-android-sdk:6.13.0"
}

object Compose {
    const val COMPILER_VERSION = "1.4.7"

    const val BOM = "androidx.compose:compose-bom:2023.04.01"

    const val RUNTIME = "androidx.compose.runtime:runtime"
    const val ANIMATIONS = "androidx.compose.animation:animation"

    // Choose one of the following:
    // Material Design 3
    const val MATERIAL_YOU = "androidx.compose.material3:material3"
    // or Material Design 2
    const val MATERIAL = "androidx.compose.material:material"
    // or skip Material Design and build directly on top of foundational components
    const val FOUNDATION = "androidx.compose.foundation:foundation"
    // or only import the main APIs for the underlying toolkit systems,
    // such as input and measurement/layout
    const val UI = "androidx.compose.ui:ui"

    // Android Studio Preview support
    const val UI_TOOLING = "androidx.compose.ui:ui-tooling"
    const val UI_TOOLING_PREVIEW = "androidx.compose.ui:ui-tooling-preview"

    // Optional - Included automatically by material, only add when you need
    // the icons but not the material library (e.g. when using Material3 or a
    // custom design system based on Foundation)
    const val ICONS_CORE = "androidx.compose.material:material-icons-core"
    // Optional - Add full set of material icons
    const val ICONS_EXTENDED = "androidx.compose.material:material-icons-extended"
    // Optional - Add window size utils
    const val WINDOW_SIZE = "androidx.compose.material3:material3-window-size-class"

    // Optional - Integration with activities
    const val ACTIVITY = "androidx.activity:activity-compose:1.7.2"
    // Optional - Integration with ViewModels
    const val VIEWMODELS = "androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1"

    // collectAsStateWithLifecycle extension
    const val LIFECYCLE = "androidx.lifecycle:lifecycle-runtime-compose:2.6.1"

    // Accompanist
    object ACCOMPANIST {
        private const val VERSION = "0.30.1"

        const val SWIPE_REFRESH = "com.google.accompanist:accompanist-swiperefresh:$VERSION"
        const val PAGER = "com.google.accompanist:accompanist-pager:$VERSION"
        const val PAGER_INDICATORS = "com.google.accompanist:accompanist-pager-indicators:$VERSION"
    }

    const val LOTTIE_COMPOSE = "com.airbnb.android:lottie-compose:6.0.0"
}
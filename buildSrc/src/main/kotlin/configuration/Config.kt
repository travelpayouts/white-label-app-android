package configuration

import com.android.builder.core.DefaultApiVersion
import com.android.builder.model.ApiVersion

/**
 * Primary config for all the stuff configured in 'build.gradle'
 */
object Config {

    val APPLICATION_ID = ApplicationVersions.APPLICATION_ID

    val VERSION_CODE = ApplicationVersions.APP_VERSION_CODE

    val VERSION_NAME = ApplicationVersions.APP_VERSION_NAME

    val minSdk: Int = 26

    // Google Play requires targetSdk 36 for new apps and updates from
    // 31 August 2026. Raising it opts the app into Android 16 behavior:
    //  - the portrait lock in the manifest is ignored on displays 600dp and
    //    wider, so the app is laid out full-screen on tablets and foldables;
    //  - onBackPressed() is no longer called and KEYCODE_BACK is no longer
    //    dispatched — code added to this template must use
    //    OnBackPressedDispatcher (the bundled SDK already does);
    //  - the edge-to-edge opt-out (windowOptOutEdgeToEdgeEnforcement) stops
    //    working. Edge-to-edge itself is not new here: it was already enforced
    //    at targetSdk 35 and the SDK theme draws that way regardless.
    val targetSdk: Int = 36

    // Kept as its own literal rather than following targetSdk: the bundled
    // travel-sdk AAR declares minCompileSdk=36, so this must stay at 36 even
    // if targetSdk is ever rolled back.
    val compileSdk: Int = 36

    const val SUPPORT_LIBRARY_VECTOR_DRAWABLES = true

}
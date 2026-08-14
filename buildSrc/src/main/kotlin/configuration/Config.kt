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

    val targetSdk: Int = 35

    // Since 1.7.0 travel-sdk is built against Android 16 and requires the app to
    // compile against API 36. That is the only requirement: targetSdk stays at 35
    // so runtime behavior on devices is unchanged. Hence compileSdk is declared
    // separately instead of following targetSdk.
    val compileSdk: Int = 36

    const val SUPPORT_LIBRARY_VECTOR_DRAWABLES = true

}
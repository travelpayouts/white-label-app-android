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

    val compileSdk = targetSdk

    const val SUPPORT_LIBRARY_VECTOR_DRAWABLES = true

}
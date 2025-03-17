package configuration

import java.io.FileInputStream
import java.util.*

object ApplicationVersions {

    const val FILENAME = "app_version.properties"
    const val PROP_BUILD_NUMBER = "buildNumber"

    private const val PROP_APPLICATION_ID = "applicationId"

    private const val PROP_VERSION_CODE = "versionCode"
    private const val PROP_VERSION = "version"
    private const val PROP_BUILD_TYPE = "buildTypeName"

    private val prop: Properties
        get() = Properties().apply {
            val fis = FileInputStream(FILENAME)
            load(fis)
            fis.close()
        }


    private val version: String = prop.getProperty(PROP_VERSION)
    private val buildType: String = prop.getProperty(PROP_BUILD_TYPE)
    private val buildNumber: String = prop.getProperty(PROP_BUILD_NUMBER)

    val APP_VERSION_CODE = prop.getProperty(PROP_VERSION_CODE).toInt()

    val APP_VERSION_NAME = version

    val VERSION_NAME_SUFFIX = buildType + buildNumber

    val FULL_APP_VERSION_NAME = APP_VERSION_NAME + VERSION_NAME_SUFFIX

    val APPLICATION_ID = prop.getProperty(PROP_APPLICATION_ID)

}


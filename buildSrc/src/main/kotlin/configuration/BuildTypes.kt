package configuration

/**
 * @author Anatolii Shulipov (as@cleverpumpkin.ru)
 */

sealed class BuildTypes(val name: String, val ribbonColor: String) {

    // Optional build type for development needs
    object DEBUG : BuildTypes("debug", "#b4f4511e")

    // Debug build type for QA
    object QA : BuildTypes("qa", "#b4ffb300")

    // Debug build type with release settings
    object RC : BuildTypes("rc", "#b47cb342")

    // Release build for uploading to app store
    object RELEASE : BuildTypes("release", "")

}
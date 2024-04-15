package util

import com.android.build.gradle.internal.dsl.BuildType

/**
 * Created by Ruslan Arslanov on 22/12/2019.
 */

fun BuildType.stringConfigField(name: String, value: String) {
    buildConfigField("String", name, "\"$value\"")
}

fun BuildType.booleanConfigField(name: String, value: Boolean) {
    buildConfigField("boolean", name, value.toString())
}

fun BuildType.longConfigField(name: String, value: Long) {
    buildConfigField("Long", name, value.toString())
}

fun BuildType.intConfigField(name: String, value: Int) {
    buildConfigField("Integer", name, value.toString())
}

fun BuildType.floatConfigField(name: String, value: Float) {
    buildConfigField("Float", name, value.toString())
}
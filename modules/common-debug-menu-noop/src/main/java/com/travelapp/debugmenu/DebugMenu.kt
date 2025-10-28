package com.travelapp.debugmenu

import android.content.Context
import androidx.annotation.Keep
import androidx.fragment.app.FragmentManager

@Suppress("UNUSED_PARAMETER")
@Keep
object DebugMenu {

    const val SHAPE_KEY = "shape"
    const val ICONS_KEY = "icons"
    const val PUSH_TOKEN_KEY = "push_token"

    fun open(fm: FragmentManager, containerViewId: Int) {
        // NOOP
    }

    fun getDebugValue(context: Context, key: String, default: String): String {
        // NOOP
        return ""
    }

    fun setDebugValue(context: Context, key: String, default: String) {
        // NOOP
    }

    fun getShowAds(context: Context): Boolean = true

}

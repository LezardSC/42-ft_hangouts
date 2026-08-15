package com.fortytwo.hangouts

import android.content.Context
import android.graphics.Color

class Prefs(context: Context) {
    private val sp = context.getSharedPreferences("hangouts_prefs", Context.MODE_PRIVATE)

    var headerColor: Int
        get() = sp.getInt(KEY_HEADER_COLOR, Color.parseColor("#2E8B84"))
        set(value) = sp.edit().putInt(KEY_HEADER_COLOR, value).apply()

    var lastBackgroundAt: Long?
        get() = sp.getLong(KEY_LAST_BACKGROUND, -1L).takeIf { it != -1L }
        set(value) = sp.edit().putLong(KEY_LAST_BACKGROUND, value ?: -1L).apply()

    companion object {
        private const val KEY_HEADER_COLOR = "header_color"
        private const val KEY_LAST_BACKGROUND = "last_background"
    }
}
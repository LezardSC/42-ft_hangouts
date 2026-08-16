package com.fortytwo.hangouts

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatDelegate

class Prefs(context: Context) {
    private val sp = context.getSharedPreferences("hangouts_prefs", Context.MODE_PRIVATE)

    var headerColor: Int
        get() = sp.getInt(KEY_HEADER_COLOR, Color.parseColor("#2E8B84"))
        set(value) = sp.edit().putInt(KEY_HEADER_COLOR, value).apply()

    var themeMode: Int
        get() = sp.getInt(KEY_THEME_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        set(value) = sp.edit().putInt(KEY_THEME_MODE, value).apply()

    var lastBackgroundedAt: Long?
        get() = sp.getLong(KEY_LAST_BACKGROUNDED, -1L).takeIf { it != -1L }
        set(value) = sp.edit().putLong(KEY_LAST_BACKGROUNDED, value ?: -1L).apply()

    companion object {
        private const val KEY_HEADER_COLOR = "header_color"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_LAST_BACKGROUNDED = "last_backgrounded"
    }
}
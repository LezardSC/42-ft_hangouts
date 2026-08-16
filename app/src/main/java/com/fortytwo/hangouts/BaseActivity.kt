package com.fortytwo.hangouts

import android.app.Activity
import android.content.Context

/** Applies the saved theme/language override on creation, and recreates if it changes while backgrounded. */
open class BaseActivity : Activity() {
    private var appliedThemeMode: Int = ThemeMode.FOLLOW_SYSTEM
    private var appliedLanguageTag: String = ""

    override fun attachBaseContext(newBase: Context) {
        val prefs = Prefs(newBase)
        appliedThemeMode = prefs.themeMode
        appliedLanguageTag = prefs.languageTag
        super.attachBaseContext(AppearanceUtils.wrapContext(newBase))
    }

    override fun onResume() {
        super.onResume()
        val prefs = Prefs(this)
        if (prefs.themeMode != appliedThemeMode || prefs.languageTag != appliedLanguageTag) {
            recreate()
        }
    }
}

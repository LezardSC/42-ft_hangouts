package com.fortytwo.hangouts

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.View
import android.view.WindowInsetsController
import android.widget.Toolbar
import java.util.Locale

object AppearanceUtils {

    /** Wraps [base] with a Configuration reflecting the saved theme/language override, if any. */
    fun wrapContext(base: Context): Context {
        val prefs = Prefs(base)
        val config = Configuration(base.resources.configuration)
        var changed = false

        when (prefs.themeMode) {
            ThemeMode.LIGHT -> {
                config.uiMode = (config.uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or
                    Configuration.UI_MODE_NIGHT_NO
                changed = true
            }
            ThemeMode.DARK -> {
                config.uiMode = (config.uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or
                    Configuration.UI_MODE_NIGHT_YES
                changed = true
            }
        }

        val tag = prefs.languageTag
        if (tag.isNotEmpty()) {
            val locale = Locale.forLanguageTag(tag)
            Locale.setDefault(locale)
            config.setLocale(locale)
            changed = true
        }

        return if (changed) base.createConfigurationContext(config) else base
    }

    /** Applies the user's chosen header color to [toolbar], keeping the status bar and icon/text contrast in sync. */
    fun applyHeaderColor(activity: Activity, toolbar: Toolbar, color: Int) {
        toolbar.background = ColorDrawable(color)
        activity.window.statusBarColor = color

        val luma = (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        val isLight = luma > 0.5

        setLightStatusBar(activity, isLight)

        val contentColor = if (isLight) Color.BLACK else Color.WHITE
        toolbar.setTitleTextColor(contentColor)
        toolbar.navigationIcon?.mutate()?.setTint(contentColor)
        for (i in 0 until toolbar.menu.size()) {
            toolbar.menu.getItem(i).icon?.mutate()?.setTint(contentColor)
        }
    }

    private fun setLightStatusBar(activity: Activity, isLight: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val appearance = if (isLight) WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS else 0
            activity.window.insetsController?.setSystemBarsAppearance(
                appearance, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            @Suppress("DEPRECATION")
            val decor = activity.window.decorView
            @Suppress("DEPRECATION")
            decor.systemUiVisibility = if (isLight) {
                decor.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                decor.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
        }
    }
}

package com.fortytwo.hangouts

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.Toolbar

class SettingsActivity : BaseActivity() {
    private lateinit var prefs: Prefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        prefs = Prefs(this)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }
        AppearanceUtils.applyHeaderColor(this, toolbar, prefs.headerColor)

        findViewById<Button>(R.id.buttonHeaderColor).setOnClickListener {
            showColorPicker()
        }

        findViewById<Button>(R.id.buttonLanguage).setOnClickListener {
            showLanguagePicker()
        }

        findViewById<Button>(R.id.buttonTheme).setOnClickListener {
            showThemePicker()
        }
    }

    private fun showColorPicker() {
        val colors = linkedMapOf(
            "Teal" to Color.parseColor("#2E8B84"),
            "Indigo" to Color.parseColor("#3F51B5"),
            "Crimson" to Color.parseColor("#B4562A"),
            "Slate" to Color.parseColor("#1B1F27")
        )
        val names = colors.keys.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle(R.string.choose_header_color)
            .setItems(names) { _, which ->
                prefs.headerColor = colors.getValue(names[which])
                AppearanceUtils.applyHeaderColor(this, findViewById(R.id.toolbar), prefs.headerColor)
            }.show()
    }

    private fun showLanguagePicker() {
        val tags = linkedMapOf(
            getString(R.string.language_system_default) to "",
            getString(R.string.language_english) to "en",
            getString(R.string.language_french) to "fr"
        )
        val names = tags.keys.toTypedArray()
        val currentIndex = tags.values.indexOfFirst { it == prefs.languageTag }.coerceAtLeast(0)

        AlertDialog.Builder(this)
            .setTitle(R.string.choose_language)
            .setSingleChoiceItems(names, currentIndex) { dialog, which ->
                prefs.languageTag = tags.getValue(names[which])
                dialog.dismiss()
                recreate()
            }.show()
    }

    private fun showThemePicker() {
        val modes = linkedMapOf(
            getString(R.string.theme_system_default) to ThemeMode.FOLLOW_SYSTEM,
            getString(R.string.theme_light) to ThemeMode.LIGHT,
            getString(R.string.theme_dark) to ThemeMode.DARK
        )
        val names = modes.keys.toTypedArray()
        val currentIndex = modes.values.indexOfFirst { it == prefs.themeMode }.coerceAtLeast(0)

        AlertDialog.Builder(this)
            .setTitle(R.string.choose_theme)
            .setSingleChoiceItems(names, currentIndex) { dialog, which ->
                prefs.themeMode = modes.getValue(names[which])
                dialog.dismiss()
                recreate()
            }.show()
    }
}

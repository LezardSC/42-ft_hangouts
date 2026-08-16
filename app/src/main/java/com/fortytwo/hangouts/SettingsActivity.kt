package com.fortytwo.hangouts

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.os.LocaleListCompat
import com.google.android.material.button.MaterialButton

class SettingsActivity : AppCompatActivity() {
    private lateinit var prefs: Prefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        prefs = Prefs(this)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }
        applyHeaderColor(toolbar, prefs.headerColor)

        findViewById<MaterialButton>(R.id.buttonHeaderColor).setOnClickListener {
            showColorPicker()
        }

        findViewById<MaterialButton>(R.id.buttonLanguage).setOnClickListener {
            showLanguagePicker()
        }

        findViewById<MaterialButton>(R.id.buttonTheme).setOnClickListener {
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
                applyHeaderColor(findViewById(R.id.toolbar), prefs.headerColor)
            }.show()
    }

    private fun showLanguagePicker() {
        val tags = linkedMapOf(
            getString(R.string.language_system_default) to "",
            getString(R.string.language_english) to "en",
            getString(R.string.language_french) to "fr"
        )
        val names = tags.keys.toTypedArray()
        val currentTag = AppCompatDelegate.getApplicationLocales().toLanguageTags()
        val currentIndex = tags.values.indexOfFirst { it == currentTag }.coerceAtLeast(0)

        AlertDialog.Builder(this)
            .setTitle(R.string.choose_language)
            .setSingleChoiceItems(names, currentIndex) { dialog, which ->
                val tag = tags.getValue(names[which])
                val locales = if (tag.isEmpty()) {
                    LocaleListCompat.getEmptyLocaleList()
                } else {
                    LocaleListCompat.forLanguageTags(tag)
                }
                AppCompatDelegate.setApplicationLocales(locales)
                dialog.dismiss()
            }.show()
    }

    private fun showThemePicker() {
        val modes = linkedMapOf(
            getString(R.string.theme_system_default) to AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
            getString(R.string.theme_light) to AppCompatDelegate.MODE_NIGHT_NO,
            getString(R.string.theme_dark) to AppCompatDelegate.MODE_NIGHT_YES
        )
        val names = modes.keys.toTypedArray()
        val currentIndex = modes.values.indexOfFirst { it == prefs.themeMode }.coerceAtLeast(0)

        AlertDialog.Builder(this)
            .setTitle(R.string.choose_theme)
            .setSingleChoiceItems(names, currentIndex) { dialog, which ->
                val mode = modes.getValue(names[which])
                prefs.themeMode = mode
                AppCompatDelegate.setDefaultNightMode(mode)
                dialog.dismiss()
            }.show()
    }
}

package com.fortytwo.hangouts

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

class SettingsActivity : AppCompatActivity() {
    private lateinit var prefs: Prefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        prefs = Prefs(this)

        findViewById<Button>(R.id.buttonHeaderColor).setOnClickListener {
            showColorPicker()
        }

        findViewById<Button>(R.id.buttonLanguage).setOnClickListener {
            showLanguagePicker()
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
}

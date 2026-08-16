package com.fortytwo.hangouts

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.WindowCompat

fun AppCompatActivity.applyHeaderColor(toolbar: Toolbar, color: Int) {
    toolbar.background = ColorDrawable(color)
    window.statusBarColor = color

    val luma = (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
    val isLight = luma > 0.5
    WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = isLight

    val contentColor = if (isLight) Color.BLACK else Color.WHITE
    toolbar.setTitleTextColor(contentColor)
    toolbar.navigationIcon?.mutate()?.setTint(contentColor)
    for (i in 0 until toolbar.menu.size()) {
        toolbar.menu.getItem(i).icon?.mutate()?.setTint(contentColor)
    }
}

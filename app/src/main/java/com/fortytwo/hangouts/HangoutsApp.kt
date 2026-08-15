package com.fortytwo.hangouts

import android.app.Application
import android.icu.text.DateFormat
import android.widget.Toast
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import java.util.Date

class HangoutsApp : Application(), DefaultLifecycleObserver {
    private lateinit var prefs: Prefs

    override fun onCreate() {
        super<Application>.onCreate()
        prefs = Prefs(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        val last = prefs.lastBackgroundedAt ?: return
        val formatted = DateFormat.getDateTimeInstance().format(Date(last))
        Toast.makeText(this, getString(R.string.last_backgrounded, formatted), Toast.LENGTH_LONG).show()
    }

    override fun onStop(owner: LifecycleOwner) {
        prefs.lastBackgroundedAt = System.currentTimeMillis()
    }
}

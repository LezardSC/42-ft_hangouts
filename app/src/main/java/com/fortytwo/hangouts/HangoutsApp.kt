package com.fortytwo.hangouts

import android.app.Activity
import android.app.Application
import android.content.Context
import android.icu.text.DateFormat
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import java.util.Date

class HangoutsApp : Application() {
    private lateinit var prefs: Prefs
    private var startedActivityCount = 0
    private val handler = Handler(Looper.getMainLooper())
    private var pendingBackgroundRunnable: Runnable? = null

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(AppearanceUtils.wrapContext(base))
    }

    override fun onCreate() {
        super.onCreate()
        prefs = Prefs(this)

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityStarted(activity: Activity) {
                startedActivityCount++

                // A stop-then-start within the grace period is just an internal transition
                // (activity switch, or recreate() from a theme/language change) - not a real backgrounding.
                pendingBackgroundRunnable?.let {
                    handler.removeCallbacks(it)
                    pendingBackgroundRunnable = null
                    return
                }

                if (startedActivityCount == 1) {
                    val last = prefs.lastBackgroundedAt
                    if (last != null) {
                        val formatted = DateFormat.getDateTimeInstance().format(Date(last))
                        Toast.makeText(
                            this@HangoutsApp, getString(R.string.last_backgrounded, formatted), Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onActivityStopped(activity: Activity) {
                startedActivityCount--
                if (startedActivityCount == 0) {
                    val runnable = Runnable {
                        pendingBackgroundRunnable = null
                        prefs.lastBackgroundedAt = System.currentTimeMillis()
                    }
                    pendingBackgroundRunnable = runnable
                    handler.postDelayed(runnable, BACKGROUND_GRACE_MS)
                }
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }

    companion object {
        // Same grace window androidx.lifecycle.process uses internally to distinguish a real
        // backgrounding from a stop-then-start caused by rotation, recreate(), or activity switches.
        private const val BACKGROUND_GRACE_MS = 700L
    }
}

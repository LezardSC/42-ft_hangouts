package com.fortytwo.hangouts

import android.Manifest
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.icu.text.DateFormat
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.util.Date

class MainActivity : AppCompatActivity() {
    private lateinit var db: DatabaseHelper
    private lateinit var prefs: Prefs
    private lateinit var adapter: ContactAdapter

    private val requestSmsPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        db = DatabaseHelper(this)
        prefs = Prefs(this)
        applyHeaderColor()

        val listView = findViewById<ListView>(R.id.listContacts)
        adapter = ContactAdapter(this, emptyList())
        listView.adapter = adapter

        findViewById<View>(R.id.buttonAddContact).setOnClickListener {
            startActivity(Intent(this, ContactFormActivity::class.java))
        }

        listView.setOnItemLongClickListener { _, _, position, _ ->
            val contact = adapter.getItem(position) ?: return@setOnItemLongClickListener false
            startActivity(Intent(this, ContactFormActivity::class.java).apply {
                putExtra(EXTRA_CONTACT_ID, contact.id)
            })
            true
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val contact = adapter.getItem(position) ?: return@setOnItemClickListener
            startActivity(Intent(this, ConversationActivity::class.java).apply {
                putExtra(EXTRA_CONTACT_ID, contact.id)
            })
        }

        requestSmsPermissions.launch(
            arrayOf(
                Manifest.permission.SEND_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_SMS,
            )
        )
    }

    override fun onResume() {
        super.onResume()
        adapter.update(db.getAllContacts())
        applyHeaderColor()
        showLastBackgroundedToast()
    }

    override fun onPause() {
        super.onPause()
        prefs.lastBackgroundedAt = System.currentTimeMillis()
    }

    private fun showLastBackgroundedToast() {
        val last = prefs.lastBackgroundedAt ?: return
        val formatted = DateFormat.getDateTimeInstance().format(Date(last))

        Toast.makeText(this, getString(R.string.last_backgrounded, formatted), Toast.LENGTH_LONG).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_settings) {
            startActivity(Intent(this, SettingsActivity::class.java))

            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun applyHeaderColor() {
        supportActionBar?.setBackgroundDrawable(ColorDrawable(prefs.headerColor))
    }

    companion object {
        const val EXTRA_CONTACT_ID = "extra_contact_id"
    }
}
package com.fortytwo.hangouts

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.Toolbar

class MainActivity : BaseActivity() {
    private lateinit var db: DatabaseHelper
    private lateinit var prefs: Prefs
    private lateinit var adapter: ContactAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setActionBar(findViewById(R.id.toolbar))

        db = DatabaseHelper(this)
        prefs = Prefs(this)
        AppearanceUtils.applyHeaderColor(this, findViewById<Toolbar>(R.id.toolbar), prefs.headerColor)

        val listView = findViewById<ListView>(R.id.listContacts)
        adapter = ContactAdapter(this, emptyList())
        listView.adapter = adapter

        findViewById<View>(R.id.buttonAddContact).setOnClickListener {
            startActivity(Intent(this, ContactFormActivity::class.java))
        }

        listView.setOnItemLongClickListener { _, _, position, _ ->
            val contact = adapter.getItem(position) ?: return@setOnItemLongClickListener false
            startActivity(Intent(this, ContactProfileActivity::class.java).apply {
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

        requestPermissions(
            arrayOf(
                Manifest.permission.SEND_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_SMS,
            ),
            REQUEST_SMS_PERMISSIONS
        )
    }

    override fun onResume() {
        super.onResume()
        adapter.update(db.getAllContacts())
        AppearanceUtils.applyHeaderColor(this, findViewById<Toolbar>(R.id.toolbar), prefs.headerColor)
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

    companion object {
        const val EXTRA_CONTACT_ID = "extra_contact_id"
        private const val REQUEST_SMS_PERMISSIONS = 1
    }
}

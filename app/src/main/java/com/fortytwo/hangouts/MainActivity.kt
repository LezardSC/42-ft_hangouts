package com.fortytwo.hangouts

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

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
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_header_color) {
            showColorPicker()

            return true
        }
        return super.onOptionsItemSelected(item)
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
                applyHeaderColor()
            }.show()
    }

    private fun applyHeaderColor() {
        supportActionBar?.setBackgroundDrawable(ColorDrawable(prefs.headerColor))
    }

    companion object {
        const val EXTRA_CONTACT_ID = "extra_contact_id"
    }
}
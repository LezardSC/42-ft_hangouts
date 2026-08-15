package com.fortytwo.hangouts

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var db: DatabaseHelper
    private lateinit var adapter: ContactAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = DatabaseHelper(this)

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
    }

    override fun onResume() {
        super.onResume()
        adapter.update(db.getAllContacts())
    }

    companion object {
        const val EXTRA_CONTACT_ID = "extra_contact_id"
    }
}
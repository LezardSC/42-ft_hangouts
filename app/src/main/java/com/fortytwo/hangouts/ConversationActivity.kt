package com.fortytwo.hangouts

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class ConversationActivity: AppCompatActivity() {
    private lateinit var db: DatabaseHelper
    private lateinit var contact: Contact
    private lateinit var adapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)

        db = DatabaseHelper(this)
        val contactId = intent.getLongExtra(MainActivity.EXTRA_CONTACT_ID, -1L)
        contact = db.getContact(contactId) ?: run { finish(); return }
        title = contact.displayName

        val listView = findViewById<ListView>(R.id.listMessages)
        adapter = MessageAdapter(this, emptyList())
        listView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun refresh() {
        adapter.update(db.getMessages(contact.id))
    }
}
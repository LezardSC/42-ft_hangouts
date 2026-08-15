package com.fortytwo.hangouts

import android.os.Bundle
import android.telephony.SmsManager
import android.view.View
import android.widget.EditText
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

        findViewById<View>(R.id.buttonSend).setOnClickListener {
            val input = findViewById<EditText>(R.id.inputMessage)
            val body = input.text.toString().trim()
            if (body.isNotEmpty()) {
                sendMessage(body)
                input.text.clear()
                refresh()
            }
        }
    }

    private fun sendMessage(body: String) {
        val smsManager = SmsManager.getDefault()
        val parts = smsManager.divideMessage(body)
        if (parts.size > 1) {
            smsManager.sendMultipartTextMessage(contact.phoneNumber, null, parts, null, null)
        } else {
            smsManager.sendTextMessage(contact.phoneNumber, null, body, null, null)
        }
        db.insertMessage(
            Message(
                contactId = contact.id,
                body = body,
                timestamp = System.currentTimeMillis(),
                direction = Direction.SENT
            )
        )
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun refresh() {
        adapter.update(db.getMessages(contact.id))
    }
}
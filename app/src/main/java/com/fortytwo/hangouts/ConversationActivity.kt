package com.fortytwo.hangouts

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat

class ConversationActivity: AppCompatActivity() {
    private lateinit var db: DatabaseHelper
    private lateinit var contact: Contact
    private lateinit var adapter: MessageAdapter

    private val requestCallPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            placeCall()
        } else {
            Toast.makeText(this, R.string.call_permission_denied, Toast.LENGTH_SHORT).show()
            openDialer()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)

        db = DatabaseHelper(this)
        val contactId = intent.getLongExtra(MainActivity.EXTRA_CONTACT_ID, -1L)
        contact = db.getContact(contactId) ?: run { finish(); return }

        setSupportActionBar(findViewById<Toolbar>(R.id.toolbar))
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.conversation_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_call) {
            onCallClicked()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onCallClicked() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
            == PackageManager.PERMISSION_GRANTED
        ) {
            placeCall()
        } else {
            requestCallPermission.launch(Manifest.permission.CALL_PHONE)
        }
    }

    private fun placeCall() {
        startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:${contact.phoneNumber}")))
    }

    private fun openDialer() {
        startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contact.phoneNumber}")))
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
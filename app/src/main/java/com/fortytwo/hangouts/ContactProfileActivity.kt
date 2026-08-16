package com.fortytwo.hangouts

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class ContactProfileActivity : AppCompatActivity() {
    private lateinit var db: DatabaseHelper
    private lateinit var prefs: Prefs
    private var contactId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_profile)

        db = DatabaseHelper(this)
        prefs = Prefs(this)
        contactId = intent.getLongExtra(MainActivity.EXTRA_CONTACT_ID, -1L)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        findViewById<View>(R.id.buttonEdit).setOnClickListener {
            startActivity(Intent(this, ContactFormActivity::class.java).apply {
                putExtra(MainActivity.EXTRA_CONTACT_ID, contactId)
            })
        }
    }

    override fun onResume() {
        super.onResume()
        val contact = db.getContact(contactId) ?: run { finish(); return }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        applyHeaderColor(toolbar, prefs.headerColor)
        title = contact.displayName

        findViewById<TextView>(R.id.textName).text = contact.displayName
        findViewById<TextView>(R.id.textPhone).text = contact.phoneNumber
        PhotoUtils.bindAvatar(findViewById<ImageView>(R.id.imageContactPhoto), contact.photoUri, contact.displayName, PHOTO_SIZE)

        findViewById<View>(R.id.rowEmail).visibility = if (contact.email.isNullOrBlank()) View.GONE else View.VISIBLE
        findViewById<TextView>(R.id.textEmail).text = contact.email

        findViewById<View>(R.id.rowNote).visibility = if (contact.note.isNullOrBlank()) View.GONE else View.VISIBLE
        findViewById<TextView>(R.id.textNote).text = contact.note
    }

    companion object {
        private const val PHOTO_SIZE = 400
    }
}

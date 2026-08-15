package com.fortytwo.hangouts

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ContactFormActivity: AppCompatActivity() {
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_form)
        db = DatabaseHelper(this)

        findViewById<View>(R.id.buttonSave).setOnClickListener { save() }
    }

    private fun save() {
        val firstName = findViewById<EditText>(R.id.inputFirstName).text.toString().trim()
        val lastName = findViewById<EditText>(R.id.inputLastName).text.toString().trim()
        val phone = findViewById<EditText>(R.id.inputPhone).text.toString().trim()

        if (firstName.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, R.string.error_required_fields, Toast.LENGTH_SHORT).show()
            return
        }

        val contact = Contact(
            firstName = firstName,
            lastName = lastName,
            phoneNumber = phone,
            email = findViewById<EditText>(R.id.inputEmail).text.toString().trim().ifEmpty { null },
            note = findViewById<EditText>(R.id.inputNote).text.toString().trim().ifEmpty { null }
        )

        db.insertContact(contact)
        finish()
    }
}
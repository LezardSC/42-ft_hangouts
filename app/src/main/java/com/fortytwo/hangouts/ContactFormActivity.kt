package com.fortytwo.hangouts

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class ContactFormActivity: AppCompatActivity() {
    private lateinit var db: DatabaseHelper
    private var editingId: Long = -1L
    private var editingPhotoUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_form)
        db = DatabaseHelper(this)

        editingId = intent.getLongExtra(MainActivity.EXTRA_CONTACT_ID, -1L)
        if (editingId != -1L) {
            db.getContact(editingId)?.let {
                fillForm(it)
                editingPhotoUri = it.photoUri
            }
            findViewById<View>(R.id.buttonDelete).visibility = View.VISIBLE
        }

        findViewById<View>(R.id.buttonSave).setOnClickListener { save() }
        findViewById<View>(R.id.buttonDelete).setOnClickListener { confirmDelete() }
    }

    private fun fillForm(contact: Contact) {
        findViewById<EditText>(R.id.inputFirstName).setText(contact.firstName)
        findViewById<EditText>(R.id.inputLastName).setText(contact.lastName)
        findViewById<EditText>(R.id.inputPhone).setText(contact.phoneNumber)
        findViewById<EditText>(R.id.inputEmail).setText(contact.email)
        findViewById<EditText>(R.id.inputNote).setText(contact.note)
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
            id = if (editingId != -1L) editingId else 0,
            firstName = firstName,
            lastName = lastName,
            phoneNumber = phone,
            email = findViewById<EditText>(R.id.inputEmail).text.toString().trim().ifEmpty { null },
            note = findViewById<EditText>(R.id.inputNote).text.toString().trim().ifEmpty { null },
            photoUri = editingPhotoUri
        )

        if (editingId != -1L) {
            db.updateContract(contact)
        } else {
            db.insertContact(contact)
        }
        finish()
    }

    private fun confirmDelete() {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_contact)
            .setMessage(R.string.delete_contact_confirm)
            .setPositiveButton(R.string.delete) { _, _ ->
                db.deleteContact(editingId)
                finish()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}
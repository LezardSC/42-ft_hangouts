package com.fortytwo.hangouts

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class ContactFormActivity: AppCompatActivity() {
    private lateinit var db: DatabaseHelper
    private var editingId: Long = -1L
    private var editingPhotoUri: String? = null

    private val pickPhoto = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            PhotoUtils.takePersistablePermission(this, uri)
            PhotoUtils.releasePersistedPermission(this, editingPhotoUri)
            editingPhotoUri = uri.toString()
            bindPhoto()
        }
    }

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
        savedInstanceState?.getString(KEY_PHOTO_URI)?.let { editingPhotoUri = it }
        bindPhoto()

        findViewById<View>(R.id.imageContactPhoto).setOnClickListener {
            pickPhoto.launch(arrayOf("image/*"))
        }
        findViewById<View>(R.id.buttonSave).setOnClickListener { save() }
        findViewById<View>(R.id.buttonDelete).setOnClickListener { confirmDelete() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_PHOTO_URI, editingPhotoUri)
    }

    private fun bindPhoto() {
        PhotoUtils.bindPhoto(findViewById<ImageView>(R.id.imageContactPhoto), editingPhotoUri, PHOTO_PREVIEW_SIZE)
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
            db.updateContact(contact)
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
                PhotoUtils.releasePersistedPermission(this, editingPhotoUri)
                finish()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    companion object {
        private const val PHOTO_PREVIEW_SIZE = 300
        private const val KEY_PHOTO_URI = "editing_photo_uri"
    }
}
package com.fortytwo.hangouts

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context): SQLiteOpenHelper(context, DbContract.DB_NAME, null, DbContract.DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE ${DbContract.Contacts.TABLE} (
                ${DbContract.Contacts.ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${DbContract.Contacts.FIRST_NAME} TEXT NOT NULL,
                ${DbContract.Contacts.LAST_NAME} TEXT NOT NULL,
                ${DbContract.Contacts.PHONE} TEXT NOT NULL,
                ${DbContract.Contacts.EMAIL} TEXT,
                ${DbContract.Contacts.NOTE} TEXT,
                ${DbContract.Contacts.PHOTO_URI} TEXT
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${DbContract.Contacts.TABLE}")

        onCreate(db)
    }

    // --- contacts ---

    fun insertContact(contact: Contact): Long {
        val values = ContentValues().apply {
            put(DbContract.Contacts.FIRST_NAME, contact.firstName)
            put(DbContract.Contacts.LAST_NAME, contact.lastName)
            put(DbContract.Contacts.PHONE, normalizeNumber(contact.phoneNumber))
            put(DbContract.Contacts.EMAIL, contact.email)
            put(DbContract.Contacts.NOTE, contact.note)
            put(DbContract.Contacts.PHOTO_URI, contact.photoUri)
        }

        return writableDatabase.insert(DbContract.Contacts.TABLE, null, values)
    }

    fun updateContract(contact: Contact) {
        val values = ContentValues().apply {
            put(DbContract.Contacts.FIRST_NAME, contact.firstName)
            put(DbContract.Contacts.LAST_NAME, contact.lastName)
            put(DbContract.Contacts.PHONE, normalizeNumber(contact.phoneNumber))
            put(DbContract.Contacts.EMAIL, contact.email)
            put(DbContract.Contacts.NOTE, contact.note)
            put(DbContract.Contacts.PHOTO_URI, contact.photoUri)
        }

        writableDatabase.update(
            DbContract.Contacts.TABLE, values,
            "${DbContract.Contacts.ID} = ?", arrayOf(contact.id.toString())
        )
    }

    fun deleteContact(id: Long) {
        writableDatabase.delete(
            DbContract.Contacts.TABLE,
            "${DbContract.Contacts.ID} = ?",
            arrayOf(id.toString())
        )
    }

    fun getAllContacts(): List<Contact> {
        val list = mutableListOf<Contact>()
        readableDatabase.query(
            DbContract.Contacts.TABLE, null, null, null, null, null, null,
            "${DbContract.Contacts.LAST_NAME} ASC"
        ).use { cursor ->
            while (cursor.moveToNext()) list.add(cursor.toContact())
        }
        return list
    }

    fun getContact(id: Long): Contact? {
        readableDatabase.query(
            DbContract.Contacts.TABLE, null,
            "${DbContract.Contacts.ID} = ?", arrayOf(id.toString()),
            null, null, null
        ).use { cursor ->
            return if (cursor.moveToFirst()) cursor.toContact() else null
        }
    }

    fun findContactByNumber(number: String): Contact? {
        val normalized = normalizeNumber(number)
        readableDatabase.query(
            DbContract.Contacts.TABLE, null,
            "${DbContract.Contacts.PHONE} = ?", arrayOf(normalized),
            null, null, null
        ).use { cursor ->
            return if (cursor.moveToFirst()) cursor.toContact() else null
        }
    }

    private fun Cursor.toContact() = Contact(
        id = getLong(getColumnIndexOrThrow(DbContract.Contacts.ID)),
        firstName = getString(getColumnIndexOrThrow(DbContract.Contacts.FIRST_NAME)),
        lastName = getString(getColumnIndexOrThrow(DbContract.Contacts.LAST_NAME)),
        phoneNumber = getString(getColumnIndexOrThrow(DbContract.Contacts.PHONE)),
        email = getString(getColumnIndexOrThrow(DbContract.Contacts.EMAIL)),
        note = getString(getColumnIndexOrThrow(DbContract.Contacts.NOTE)),
        photoUri = getString(getColumnIndexOrThrow(DbContract.Contacts.PHOTO_URI)),
    )

    companion object {
        fun normalizeNumber(raw: String): String = raw.filter { it.isDigit() || it == '+' }
    }
}

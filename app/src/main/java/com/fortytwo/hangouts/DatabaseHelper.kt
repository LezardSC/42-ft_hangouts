package com.fortytwo.hangouts

import android.content.Context
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
}
package com.fortytwo.hangouts

object DbContract {
    const val DB_NAME = "hangouts.db"
    const val DB_VERSION = 1

    object Contacts {
        const val TABLE = "contacts"
        const val ID = "_id"
        const val FIRST_NAME = "first_name"
        const val LAST_NAME = "last_name"
        const val PHONE = "phone_number"
        const val EMAIL = "email"
        const val NOTE = "note"
        const val PHOTO_URI = "photo_uri"
    }

    object Messages {
        const val TABLE = "messages"
        const val ID = "_id"
        const val CONTACT_ID = "contact_id"
        const val BODY = "body"
        const val TIMESTAMP = "timestamp"
        const val DIRECTION = "direction" // 0 = sent, 1 = received
    }
}
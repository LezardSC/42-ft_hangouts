package com.fortytwo.hangouts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony

class SmsReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val db = DatabaseHelper(context)
        val incoming = Telephony.Sms.Intents.getMessagesFromIntent(intent)

        for (sms in incoming) {
            val sender = sms.originatingAddress ?: continue
            val body = sms.messageBody ?: ""

            var contact = db.findContactByNumber(sender)
            if (contact == null) {
                // auto creates a contact for unknown numbers
                val newId = db.insertContact(
                    Contact(firstName = sender, lastName = "", phoneNumber = sender)
                )
                contact = db.getContact(newId)
            }

            contact?.let {
                db.insertMessage(
                    Message(
                        contactId = it.id,
                        body = body,
                        timestamp = System.currentTimeMillis(),
                        direction = Direction.RECEIVED
                    )
                )
            }
        }
    }
}
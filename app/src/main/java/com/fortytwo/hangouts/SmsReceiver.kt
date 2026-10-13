package com.fortytwo.hangouts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
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

        context.sendBroadcast(Intent(ACTION_MESSAGES_CHANGED).setPackage(context.packageName))
    }

    companion object {
        const val ACTION_MESSAGES_CHANGED = "com.fortytwo.hangouts.MESSAGES_CHANGED"

        fun registerForChanges(context: Context, onChanged: () -> Unit): BroadcastReceiver {
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) = onChanged()
            }
            val filter = IntentFilter(ACTION_MESSAGES_CHANGED)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
            } else {
                context.registerReceiver(receiver, filter)
            }
            return receiver
        }
    }
}
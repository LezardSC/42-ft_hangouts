package com.fortytwo.hangouts

enum class Direction { SENT, RECEIVED }

data class Message(
    val id: Long = 0,
    val contactId: Long,
    val body: String,
    val timestamp: Long,
    val direction: Direction
)
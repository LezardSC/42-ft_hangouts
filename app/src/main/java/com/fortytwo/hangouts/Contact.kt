package com.fortytwo.hangouts

data class Contact (
    val id: Long = 0,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val email: String? = null,
    val note: String? = null,
    val photoUri: String? = null
) {
    val displayName: String get() = "$firstName $lastName".trim()
}
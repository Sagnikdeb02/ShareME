package com.example.shareme.firebase

data class FirebaseNote(
    val title: String = "",
    val content: String = "",
    val color: Int = 0,
    val imageUrl: String = "" ,// URL for the image stored in Firebase Storage
    val receiverEmail: String = ""
)

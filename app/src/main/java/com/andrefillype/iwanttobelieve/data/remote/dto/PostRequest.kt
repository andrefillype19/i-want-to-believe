package com.andrefillype.iwanttobelieve.data.remote.dto

import com.google.firebase.Timestamp

data class PostRequest(
    val authorId: String,
    val authorName: String,
    val description: String,
    val imageUrl: String,
    val createdAt: Timestamp = Timestamp.now()
)

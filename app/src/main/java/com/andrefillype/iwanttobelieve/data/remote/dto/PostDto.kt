package com.andrefillype.iwanttobelieve.data.remote.dto

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class PostDto(
    @DocumentId
    val uid: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val likes: List<String> = emptyList()
)
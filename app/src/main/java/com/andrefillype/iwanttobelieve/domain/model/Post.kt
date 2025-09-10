package com.andrefillype.iwanttobelieve.domain.model

import java.util.Date

data class Post(
    val uid : String,
    val authorId: String,
    val authorName: String,
    val description: String,
    val imageUrl: String,
    val createdAt: Date,
    val likes: List<String> = emptyList()
) {
    val likesCount: Int get() = likes.size
}

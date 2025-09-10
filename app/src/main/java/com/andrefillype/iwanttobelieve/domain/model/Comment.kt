package com.andrefillype.iwanttobelieve.domain.model

import java.util.Date

data class Comment(
    val uid: String,
    val postId: String,
    val authorId: String,
    val authorName: String,
    val text: String,
    val createdAt: Date
)
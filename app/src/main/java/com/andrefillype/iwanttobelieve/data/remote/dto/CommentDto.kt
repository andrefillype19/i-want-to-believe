package com.andrefillype.iwanttobelieve.data.remote.dto

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class CommentDto(
    @DocumentId
    val uid: String = "",
    val postId: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val text: String = "",
    val createdAt: Timestamp = Timestamp.now()
) {
    constructor() : this("", "", "", "", "",Timestamp.now())
}

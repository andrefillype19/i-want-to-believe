package com.andrefillype.iwanttobelieve.data.mappers

import com.andrefillype.iwanttobelieve.data.remote.dto.CommentDto
import com.andrefillype.iwanttobelieve.domain.model.Comment

fun CommentDto.toDomain(): Comment {
    return Comment(
        uid = uid,
        postId = postId,
        authorId = authorId,
        authorName = authorName,
        text = text,
        createdAt = createdAt.toDate()
    )
}

fun List<CommentDto>.toDomain(): List<Comment> {
    return map { it.toDomain() }
}
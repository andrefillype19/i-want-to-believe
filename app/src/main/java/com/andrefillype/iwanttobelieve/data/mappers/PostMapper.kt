package com.andrefillype.iwanttobelieve.data.mappers

import com.andrefillype.iwanttobelieve.data.remote.dto.PostDto
import com.andrefillype.iwanttobelieve.data.remote.dto.PostRequest
import com.andrefillype.iwanttobelieve.domain.model.Post

fun PostDto.toDomain(): Post {
    return Post(
        uid = uid,
        authorId = authorId,
        authorName = authorName,
        description = description,
        imageUrl = imageUrl,
        createdAt = createdAt.toDate(),
        likes = likes
    )
}

fun PostRequest.toDto(): PostDto {
    return PostDto(
        uid = "",
        authorId = authorId,
        authorName = authorName,
        description = description,
        imageUrl = imageUrl,
        createdAt = createdAt,
        likes = emptyList()
    )
}

fun List<PostDto>.toDomain(): List<Post> {
    return map { it.toDomain() }
}
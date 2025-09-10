package com.andrefillype.iwanttobelieve.data.mappers

import com.andrefillype.iwanttobelieve.data.remote.dto.UserDto
import com.andrefillype.iwanttobelieve.domain.model.User
import com.google.firebase.Timestamp

fun UserDto.toDomain(): User {
    return User(
        uid = uid,
        name = name,
        email = email,
        createdAt = createdAt.toDate()
    )
}

fun User.toDto(): UserDto {
    return UserDto(
        uid = uid,
        name = name,
        email = email,
        createdAt = Timestamp(createdAt)
    )
}
package com.andrefillype.iwanttobelieve.domain.model

import java.util.Date

data class User(
    val uid: String,
    val name: String,
    val email: String,
    val createdAt: Date
)
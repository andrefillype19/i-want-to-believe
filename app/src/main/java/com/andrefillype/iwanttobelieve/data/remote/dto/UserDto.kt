package com.andrefillype.iwanttobelieve.data.remote.dto

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class UserDto(
    @DocumentId
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val createdAt: Timestamp = Timestamp.now()
) {
    constructor() : this("", "", "", Timestamp.now())
}
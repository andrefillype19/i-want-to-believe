package com.andrefillype.iwanttobelieve.domain.repository

import com.andrefillype.iwanttobelieve.domain.model.User

interface UserRepository {
    suspend fun createUserProfile(user: User): Result<Unit>
    suspend fun getUserProfile(uid: String): Result<User?>
    suspend fun getCurrentUserProfile(): Result<User?>
    suspend fun updateUserProfile(user: User): Result<Unit>
}
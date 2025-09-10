package com.andrefillype.iwanttobelieve.domain.repository

import com.andrefillype.iwanttobelieve.domain.model.User
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signUp(
        name: String,
        email: String,
        password: String
    ): Result<Unit>

    suspend fun signIn(
        email: String,
        password: String
    ): Result<Unit>

    fun getCurrentFirebaseUser(): FirebaseUser?
    suspend fun isUserActive(user: FirebaseUser): Boolean
    suspend fun signOut(): Result<Unit>
    suspend fun getCurrentUser(): Result<User?>
    fun isUserAuthenticated(): Boolean
    fun getAuthStateFlow(): Flow<Boolean>
}
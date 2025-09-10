package com.andrefillype.iwanttobelieve.data.repository

import com.andrefillype.iwanttobelieve.data.mappers.toDomain
import com.andrefillype.iwanttobelieve.data.remote.dto.UserDto
import com.andrefillype.iwanttobelieve.data.remote.firebase.FirebaseAuthService
import com.andrefillype.iwanttobelieve.data.remote.firebase.FirestoreService
import com.andrefillype.iwanttobelieve.domain.model.User
import com.andrefillype.iwanttobelieve.domain.repository.AuthRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val authService: FirebaseAuthService,
    private val firestoreService: FirestoreService
): AuthRepository {
    override suspend fun signUp(name: String, email: String, password: String): Result<Unit> {
        return try {
            val firebaseUserResult = authService.signUp(email, password)
            val firebaseUser = firebaseUserResult.getOrThrow()

            val userDto = UserDto(
                uid = firebaseUser.uid,
                name = name,
                email = email,
                createdAt = Timestamp.now()
            )

            firestoreService.createUser(userDto).getOrThrow()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            authService.signIn(email, password).getOrThrow()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            authService.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(): Result<User?> {
        return try {
            val firebaseUser = authService.getCurrentUser()

            if (firebaseUser == null) {
                Result.success(null)
            } else {
                val userDto = firestoreService.getUser(firebaseUser.uid).getOrThrow()
                Result.success(userDto?.toDomain())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun isUserAuthenticated(): Boolean {
        return authService.isUserAuthenticated()
    }

    override fun getAuthStateFlow(): Flow<Boolean> {
        return authService.getAuthStateFlow()
    }

    override fun getCurrentFirebaseUser(): FirebaseUser? {
        return authService.getCurrentUser()
    }

    override suspend fun isUserActive(user: FirebaseUser): Boolean {
        return try {
            user.reload().await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
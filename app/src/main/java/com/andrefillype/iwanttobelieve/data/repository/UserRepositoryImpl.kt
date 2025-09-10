package com.andrefillype.iwanttobelieve.data.repository

import com.andrefillype.iwanttobelieve.data.remote.firebase.FirebaseAuthService
import com.andrefillype.iwanttobelieve.data.remote.firebase.FirestoreService
import com.andrefillype.iwanttobelieve.domain.model.User
import com.andrefillype.iwanttobelieve.domain.repository.UserRepository
import com.andrefillype.iwanttobelieve.data.mappers.toDto
import com.andrefillype.iwanttobelieve.data.mappers.toDomain

class UserRepositoryImpl(
    private val firestoreService: FirestoreService,
    private val authService: FirebaseAuthService
) : UserRepository {

    override suspend fun createUserProfile(user: User): Result<Unit> {
        return try {
            val userDto = user.toDto()
            firestoreService.createUser(userDto).getOrThrow()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserProfile(uid: String): Result<User?> {
        return try {
            val userDto = firestoreService.getUser(uid).getOrThrow()
            Result.success(userDto?.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUserProfile(): Result<User?> {
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

    override suspend fun updateUserProfile(user: User): Result<Unit> {
        return try {
            val userDto = user.toDto()
            firestoreService.updateUser(userDto).getOrThrow()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

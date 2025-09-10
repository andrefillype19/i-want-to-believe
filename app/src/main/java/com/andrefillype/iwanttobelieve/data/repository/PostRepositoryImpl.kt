package com.andrefillype.iwanttobelieve.data.repository

import android.net.Uri
import com.andrefillype.iwanttobelieve.data.mappers.toDomain
import com.andrefillype.iwanttobelieve.data.remote.dto.PostRequest
import com.andrefillype.iwanttobelieve.data.remote.firebase.FirebaseAuthService
import com.andrefillype.iwanttobelieve.data.remote.firebase.FirestoreService
import com.andrefillype.iwanttobelieve.domain.model.Post
import com.andrefillype.iwanttobelieve.domain.repository.PostRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PostRepositoryImpl(
    private val firestoreService: FirestoreService,
    //private val storageService: StorageService,
    private val authService: FirebaseAuthService
) : PostRepository {

    override suspend fun createPost(description: String, imageUri: String): Result<String> {
        return try {
            val currentUser = authService.getCurrentUser()
                ?: return Result.failure(Exception("Usuário não autenticado"))

            val userDto = firestoreService.getUser(currentUser.uid).getOrThrow()
                ?: return Result.failure(Exception("Perfil do usuário não encontrado"))

            val postRequest = PostRequest(
                authorId = currentUser.uid,
                authorName = userDto.name,
                description = description,
                imageUrl = imageUri,
                createdAt = Timestamp.now()
            )

            val postId = firestoreService.createPost(postRequest).getOrThrow()
            Result.success(postId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllPosts(): Flow<List<Post>> {
        return firestoreService.getAllPosts().map { postDtos ->
            postDtos.toDomain()
        }
    }

    override suspend fun getUserPosts(userId: String): Result<List<Post>> {
        return try {
            val postsDtos = firestoreService.getUserPosts(userId).getOrThrow()
            Result.success(postsDtos.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deletePost(postId: String): Result<Unit> {
        return try {
            val currentUser = authService.getCurrentUser()
                ?: return Result.failure(Exception("Usuário não autenticado"))

            val postDto = firestoreService.getPost(postId).getOrThrow()
                ?: return Result.failure(Exception("Post não encontrado"))

            if (postDto.authorId != currentUser.uid) {
                return Result.failure((Exception("Você só pode deletar seus próprios posts")))
            }

            firestoreService.deletePost(postId).getOrThrow()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPost(postId: String): Result<Post?> {
        return try {
            val postDto = firestoreService.getPost(postId).getOrThrow()
            Result.success(postDto?.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun toggleLike(postId: String, userId: String): Result<Unit> {
        return firestoreService.toggleLike(postId, userId)
    }
}
package com.andrefillype.iwanttobelieve.domain.repository

import android.net.Uri
import com.andrefillype.iwanttobelieve.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    suspend fun createPost(
        description: String,
        imageUri: String
    ): Result<String>

    suspend fun getAllPosts(): Flow<List<Post>>
    suspend fun getUserPosts(userId: String): Result<List<Post>>
    suspend fun deletePost(postId: String): Result<Unit>
    suspend fun getPost(postId: String): Result<Post?>
    suspend fun toggleLike(postId: String, userId: String): Result<Unit>
}
package com.andrefillype.iwanttobelieve.data.remote.firebase

import com.andrefillype.iwanttobelieve.data.remote.dto.PostDto
import com.andrefillype.iwanttobelieve.data.remote.dto.PostRequest
import com.andrefillype.iwanttobelieve.data.remote.dto.UserDto
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreService {
    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    suspend fun createUser(userDto: UserDto): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(userDto.uid)
                .set(userDto)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUser(uid: String): Result<UserDto?> {
        return try {
            val document = firestore.collection("users")
                .document(uid)
                .get()
                .await()

            val user = document.toObject(UserDto::class.java)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUser(userDto: UserDto): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(userDto.uid)
                .set(userDto)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createPost(postRequest: PostRequest): Result<String> {
        return try {
            val documentRef = firestore.collection("posts")
                .add(postRequest)
                .await()
            Result.success(documentRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getAllPosts(): Flow<List<PostDto>> = callbackFlow {
        val listener = firestore.collection("posts")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val posts = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(PostDto::class.java)?.copy(uid = document.id)
                } ?: emptyList()

                trySend(posts)
            }

        awaitClose { listener.remove() }
    }

    suspend fun getUserPosts(userId: String): Result<List<PostDto>> {
        return try {
            val snapshot = firestore.collection("posts")
                .whereEqualTo("authorId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val posts = snapshot.documents.mapNotNull { document ->
                document.toObject(PostDto::class.java)?.copy(uid = document.id)
            }

            Result.success(posts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deletePost(postId: String): Result<Unit> {
        return try {
            firestore.collection("posts")
                .document(postId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPost(postId: String): Result<PostDto?> {
        return try {
            val document = firestore.collection("posts")
                .document(postId)
                .get()
                .await()

            val post = document.toObject(PostDto::class.java)?.copy(uid = document.id)
            Result.success(post)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleLike(postId: String, userId: String) : Result<Unit> {
        return try {
            val postRef = firestore.collection("posts").document(postId)
            val snapshot = postRef.get().await()
            val likes = snapshot.get("likes") as? List<String> ?: emptyList()
            val alreadyLiked = likes.contains(userId)

            if (alreadyLiked) {
                postRef.update("likes", FieldValue.arrayRemove(userId)).await()
            } else {
                postRef.update("likes", FieldValue.arrayUnion(userId)).await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
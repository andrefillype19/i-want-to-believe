package com.andrefillype.iwanttobelieve.domain.usecase.post

import com.andrefillype.iwanttobelieve.domain.model.Post
import com.andrefillype.iwanttobelieve.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow

class GetAllPostsUseCase(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(): Flow<List<Post>> {
        return postRepository.getAllPosts()
    }
}
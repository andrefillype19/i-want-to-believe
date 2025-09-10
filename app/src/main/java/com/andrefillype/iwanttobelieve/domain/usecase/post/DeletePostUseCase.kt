package com.andrefillype.iwanttobelieve.domain.usecase.post

import com.andrefillype.iwanttobelieve.domain.repository.PostRepository

class DeletePostUseCase(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(postId: String): Result<Unit> {
        return postRepository.deletePost(postId)
    }
}
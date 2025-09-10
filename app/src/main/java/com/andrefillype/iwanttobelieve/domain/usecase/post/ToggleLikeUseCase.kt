package com.andrefillype.iwanttobelieve.domain.usecase.post

import com.andrefillype.iwanttobelieve.domain.repository.PostRepository

class ToggleLikeUseCase(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(postId: String, userId: String): Result<Unit> {
        return postRepository.toggleLike(postId, userId)
    }
}

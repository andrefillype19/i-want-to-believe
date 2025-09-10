package com.andrefillype.iwanttobelieve.domain.usecase.post

import android.net.Uri
import com.andrefillype.iwanttobelieve.domain.repository.PostRepository

class CreatePostUseCase(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(
        description: String,
        imageUri: String
    ): Result<String> {
        if (description.isBlank()) {
            return Result.failure(Exception("Descrição não pode estar vazia"))
        }

        if (description.length > 500) {
            return Result.failure(Exception("Descrição deve ter no máximo 500 caracteres"))
        }

        return postRepository.createPost(description, imageUri)
    }
}
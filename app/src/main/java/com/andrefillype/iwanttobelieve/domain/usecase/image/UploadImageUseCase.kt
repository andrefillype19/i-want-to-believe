package com.andrefillype.iwanttobelieve.domain.usecase.image

import com.andrefillype.iwanttobelieve.domain.repository.UploadRepository
import java.io.File

class UploadImageUseCase(
    private val uploadRepository: UploadRepository
) {
    suspend operator fun invoke(file: File): Result<String> {
        return uploadRepository.uploadImage(file)
    }
}
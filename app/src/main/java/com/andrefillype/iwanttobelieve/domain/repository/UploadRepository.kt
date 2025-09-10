package com.andrefillype.iwanttobelieve.domain.repository

import java.io.File

interface UploadRepository {
    suspend fun uploadImage(file: File): Result<String>
}
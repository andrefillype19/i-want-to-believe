package com.andrefillype.iwanttobelieve.data.repository

import com.andrefillype.iwanttobelieve.data.remote.imgbb.ImgBBService
import com.andrefillype.iwanttobelieve.data.remote.uploadcare.UploadcareService
import com.andrefillype.iwanttobelieve.domain.repository.UploadRepository
import java.io.File

class UploadRepositoryImpl(
    private val imgbbService: ImgBBService
) : UploadRepository {

    override suspend fun uploadImage(file: File): Result<String> {
        // Encapsula a chamada ao serviço
        return imgbbService.uploadImage(file)
    }
}
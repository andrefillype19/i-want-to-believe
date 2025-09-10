package com.andrefillype.iwanttobelieve.data.repository

import com.andrefillype.iwanttobelieve.data.remote.imgbb.ImgBBService
import com.andrefillype.iwanttobelieve.domain.repository.UploadRepository
import java.io.File

class ImgBBUploadRepositoryImpl(
    private val imgBBService: ImgBBService
) : UploadRepository {

    override suspend fun uploadImage(file: File): Result<String> {
        return imgBBService.uploadImage(file)
    }
}

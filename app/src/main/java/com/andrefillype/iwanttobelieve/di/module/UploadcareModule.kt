package com.andrefillype.iwanttobelieve.di.module

import android.content.Context
import com.andrefillype.iwanttobelieve.data.remote.uploadcare.UploadcareService
import com.andrefillype.iwanttobelieve.data.repository.UploadRepositoryImpl
import com.andrefillype.iwanttobelieve.domain.repository.UploadRepository
import com.andrefillype.iwanttobelieve.domain.usecase.image.UploadImageUseCase

object UploadcareModule {

    fun provideUploadcareService(): UploadcareService {
        return UploadcareService(
            publicKey = "1d79b77cacd59387657d"
        )
    }
}
package com.andrefillype.iwanttobelieve.di.module

import com.andrefillype.iwanttobelieve.data.remote.imgbb.ImgBBService

object ImgBBModule {

    // 🔑 substitua pela sua chave de API do ImgBB
    private const val API_KEY = "691bf17b590a014621dd470ebfb8432a"

    fun provideImgBBService(): ImgBBService {
        return ImgBBService(API_KEY)
    }
}

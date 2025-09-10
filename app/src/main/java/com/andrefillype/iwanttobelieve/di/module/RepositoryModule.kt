package com.andrefillype.iwanttobelieve.di.module

import android.content.Context
import com.andrefillype.iwanttobelieve.data.repository.AuthRepositoryImpl
import com.andrefillype.iwanttobelieve.data.repository.ImgBBUploadRepositoryImpl
import com.andrefillype.iwanttobelieve.data.repository.PostRepositoryImpl
import com.andrefillype.iwanttobelieve.data.repository.UploadRepositoryImpl
import com.andrefillype.iwanttobelieve.data.repository.UserRepositoryImpl
import com.andrefillype.iwanttobelieve.domain.repository.AuthRepository
import com.andrefillype.iwanttobelieve.domain.repository.PostRepository
import com.andrefillype.iwanttobelieve.domain.repository.UploadRepository
import com.andrefillype.iwanttobelieve.domain.repository.UserRepository

object RepositoryModule {

    fun provideAuthRepository(context: Context): AuthRepository {
        return AuthRepositoryImpl(
            authService = FirebaseModule.provideFirebaseAuthService(context),
            firestoreService = FirebaseModule.provideFirestoreService()
        )
    }

    fun provideUserRepository(context: Context): UserRepository {
        return UserRepositoryImpl(
            firestoreService = FirebaseModule.provideFirestoreService(),
            authService = FirebaseModule.provideFirebaseAuthService(context)
        )
    }

    fun providePostRepository(context: Context): PostRepository {
        return PostRepositoryImpl(
            firestoreService = FirebaseModule.provideFirestoreService(),
            authService = FirebaseModule.provideFirebaseAuthService(context)
        )
    }

    fun provideUploadRepository(): UploadRepository {
        return ImgBBUploadRepositoryImpl(
            imgBBService = ImgBBModule.provideImgBBService()
        )
    }
}
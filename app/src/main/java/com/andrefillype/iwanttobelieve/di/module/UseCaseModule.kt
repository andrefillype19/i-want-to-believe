package com.andrefillype.iwanttobelieve.di.module

import android.content.Context
import com.andrefillype.iwanttobelieve.domain.usecase.auth.CheckUserActiveUseCase
import com.andrefillype.iwanttobelieve.domain.usecase.auth.GetCurrentUserUseCase
import com.andrefillype.iwanttobelieve.domain.usecase.auth.SignInUseCase
import com.andrefillype.iwanttobelieve.domain.usecase.auth.SignOutUseCase
import com.andrefillype.iwanttobelieve.domain.usecase.auth.SignUpUseCase
import com.andrefillype.iwanttobelieve.domain.usecase.image.UploadImageUseCase
import com.andrefillype.iwanttobelieve.domain.usecase.post.CreatePostUseCase
import com.andrefillype.iwanttobelieve.domain.usecase.post.DeletePostUseCase
import com.andrefillype.iwanttobelieve.domain.usecase.post.GetAllPostsUseCase
import com.andrefillype.iwanttobelieve.domain.usecase.post.ToggleLikeUseCase

object UseCaseModule {

    fun provideSignUpUseCase(context: Context): SignUpUseCase {
        return SignUpUseCase(
            authRepository = RepositoryModule.provideAuthRepository(context)
        )
    }

    fun provideSignInUseCase(context: Context): SignInUseCase {
        return SignInUseCase(
            authRepository = RepositoryModule.provideAuthRepository(context)
        )
    }

    fun provideSignOutUseCase(context: Context): SignOutUseCase {
        return SignOutUseCase(
            authRepository = RepositoryModule.provideAuthRepository(context)
        )
    }

    fun provideGetCurrentUserUseCase(context: Context): GetCurrentUserUseCase {
        return GetCurrentUserUseCase(
            authRepository = RepositoryModule.provideAuthRepository(context)
        )
    }

    fun provideCreatePostUseCase(context: Context): CreatePostUseCase {
        return CreatePostUseCase(
            postRepository = RepositoryModule.providePostRepository(context)
        )
    }

    fun provideGetAllPostsUseCase(context: Context): GetAllPostsUseCase {
        return GetAllPostsUseCase(
            postRepository = RepositoryModule.providePostRepository(context)
        )
    }

    fun provideDeletePostUseCase(context: Context): DeletePostUseCase {
        return DeletePostUseCase(
            postRepository = RepositoryModule.providePostRepository(context)
        )
    }

    fun provideCheckUserActiveUseCase(context: Context): CheckUserActiveUseCase {
        return CheckUserActiveUseCase(
            authRepository = RepositoryModule.provideAuthRepository(context)
        )
    }

    fun provideUploadImageUseCase(): UploadImageUseCase {
        return UploadImageUseCase(
            uploadRepository = RepositoryModule.provideUploadRepository()
        )
    }

    fun provideToggleLikeUseCase(context: Context): ToggleLikeUseCase {
        return ToggleLikeUseCase(
            postRepository = RepositoryModule.providePostRepository(context)
        )
    }
}
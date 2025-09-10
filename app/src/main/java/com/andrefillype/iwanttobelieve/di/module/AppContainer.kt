package com.andrefillype.iwanttobelieve.di.module

import android.content.Context
import com.andrefillype.iwanttobelieve.domain.repository.AuthRepository
import com.andrefillype.iwanttobelieve.domain.repository.PostRepository
import com.andrefillype.iwanttobelieve.domain.repository.UserRepository
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
import com.andrefillype.iwanttobelieve.presentation.viewmodel.post.PostViewModel

class AppContainer(private val context: Context) {

    val authRepository: AuthRepository by lazy {
        RepositoryModule.provideAuthRepository(context)
    }

    val userRepository: UserRepository by lazy {
        RepositoryModule.provideUserRepository(context)
    }

    val postRepository: PostRepository by lazy {
        RepositoryModule.providePostRepository(context)
    }

    val signUpUseCase: SignUpUseCase by lazy {
        UseCaseModule.provideSignUpUseCase(context)
    }

    val signInUseCase: SignInUseCase by lazy {
        UseCaseModule.provideSignInUseCase(context)
    }

    val getCurrentUserUseCase: GetCurrentUserUseCase by lazy {
        UseCaseModule.provideGetCurrentUserUseCase(context)
    }

    val checkUserActiveUseCase: CheckUserActiveUseCase by lazy {
        UseCaseModule.provideCheckUserActiveUseCase(context)
    }

    val signOutUseCase: SignOutUseCase by lazy {
        UseCaseModule.provideSignOutUseCase(context)
    }

    val createPostUseCase: CreatePostUseCase by lazy {
        UseCaseModule.provideCreatePostUseCase(context)
    }

    val getAllPostsUseCase: GetAllPostsUseCase by lazy {
        UseCaseModule.provideGetAllPostsUseCase(context)
    }

    val deletePostUseCase: DeletePostUseCase by lazy {
        UseCaseModule.provideDeletePostUseCase(context)
    }

    val uploadImageUseCase: UploadImageUseCase by lazy {
        UseCaseModule.provideUploadImageUseCase()
    }

    val toggleLikeUseCase: ToggleLikeUseCase by lazy {
        UseCaseModule.provideToggleLikeUseCase(context)
    }
}
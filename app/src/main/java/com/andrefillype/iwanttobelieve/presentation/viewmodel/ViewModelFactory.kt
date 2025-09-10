package com.andrefillype.iwanttobelieve.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.andrefillype.iwanttobelieve.di.module.AppContainer
import com.andrefillype.iwanttobelieve.presentation.viewmodel.auth.AuthViewModel
import com.andrefillype.iwanttobelieve.presentation.viewmodel.feed.FeedViewModel
import com.andrefillype.iwanttobelieve.presentation.viewmodel.post.PostViewModel
import com.andrefillype.iwanttobelieve.presentation.viewmodel.profile.ProfileViewModel
import kotlin.math.sign

class ViewModelFactory(
    private val appContainer: AppContainer
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            AuthViewModel::class.java -> {
                AuthViewModel(
                    signInUseCase = appContainer.signInUseCase,
                    signUpUseCase = appContainer.signUpUseCase,
                    getCurrentUserUseCase = appContainer.getCurrentUserUseCase,
                    signOutUseCase = appContainer.signOutUseCase
                ) as T
            }

            FeedViewModel::class.java -> {
                FeedViewModel(
                    getAllPostsUseCase = appContainer.getAllPostsUseCase,
                    deletePostUseCase = appContainer.deletePostUseCase,
                    getCurrentUserUseCase = appContainer.getCurrentUserUseCase,
                    signOutUseCase = appContainer.signOutUseCase,
                    toggleLikeUseCase = appContainer.toggleLikeUseCase
                ) as T
            }

            PostViewModel::class.java -> {
                PostViewModel(
                    createPostUseCase = appContainer.createPostUseCase,
                    uploadImageUseCase = appContainer.uploadImageUseCase
                ) as T
            }

            ProfileViewModel::class.java -> {
                ProfileViewModel(
                    getCurrentUserUseCase = appContainer.getCurrentUserUseCase,
                    signOutUseCase = appContainer.signOutUseCase
                ) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

@androidx.compose.runtime.Composable
fun <T: ViewModel> appViewModel(
    modelClass: Class<T>,
    appContainer: AppContainer
): T {
    val factory = ViewModelFactory(appContainer)
    return androidx.lifecycle.viewmodel.compose.viewModel(
        modelClass = modelClass,
        factory = factory
    )
}
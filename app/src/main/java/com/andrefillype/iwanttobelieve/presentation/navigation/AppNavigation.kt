package com.andrefillype.iwanttobelieve.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.andrefillype.iwanttobelieve.di.module.AppContainer
import com.andrefillype.iwanttobelieve.presentation.ui.LoginScreen
import com.andrefillype.iwanttobelieve.presentation.ui.SignUpScreen
import com.andrefillype.iwanttobelieve.presentation.ui.FeedScreen
import com.andrefillype.iwanttobelieve.presentation.viewmodel.appViewModel
import com.andrefillype.iwanttobelieve.presentation.viewmodel.auth.AuthViewModel
import androidx.compose.runtime.getValue
import com.andrefillype.iwanttobelieve.presentation.ui.PostScreen
import com.andrefillype.iwanttobelieve.presentation.ui.ProfileScreen
import com.andrefillype.iwanttobelieve.presentation.ui.SplashScreen


@Composable
fun AppNavigation(appContainer: AppContainer) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = appViewModel(
        modelClass = AuthViewModel::class.java,
        appContainer = appContainer
    )
    val authUiState by authViewModel.uiState.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen()

            LaunchedEffect(authUiState.isCheckingAuth) {
                if (!authUiState.isCheckingAuth) {
                    val isActive = appContainer.checkUserActiveUseCase()

                    if (isActive) {
                        navController.navigate("feed") {
                            popUpTo("splash") { inclusive = true }
                        }
                    } else {
                        navController.navigate("login") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                }
            }
        }



        composable("login") {
            LoginScreen(
                appContainer = appContainer,
                onNavigateToSignUp = { navController.navigate("signup") },
                onNavigateToHome = {
                    navController.navigate("feed") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onForgotPassword = {}
            )
        }

        composable("signup") {
            SignUpScreen(
                appContainer = appContainer,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("signup") { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate("feed") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
            )
        }

        composable("post") {
            PostScreen(
                appContainer = appContainer,
                onNavigateBack = {
                    navController.popBackStack() // volta para a FeedScreen
                },
            )
        }

        composable("profile") {
            ProfileScreen(
                appContainer = appContainer,
                onNavigateToFeed = {
                    navController.navigate("feed") {
                        popUpTo("feed") { inclusive = true } // garante que volta para a FeedScreen
                    }
                },
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("profile") { inclusive = true } // remove ProfileScreen da backstack
                    }
                }
            )
        }



        composable("feed") {
            FeedScreen(
                appContainer = appContainer,
                onNavigateToProfile = {
                    navController.navigate("profile")
                },
                onNavigateToCreatePost = {
                    navController.navigate("post")
                },
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }


    }
}

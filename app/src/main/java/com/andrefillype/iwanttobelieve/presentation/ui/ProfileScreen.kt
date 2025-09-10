package com.andrefillype.iwanttobelieve.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.andrefillype.iwanttobelieve.di.module.AppContainer
import com.andrefillype.iwanttobelieve.domain.model.Post
import com.andrefillype.iwanttobelieve.domain.model.User
import com.andrefillype.iwanttobelieve.presentation.viewmodel.appViewModel
import com.andrefillype.iwanttobelieve.presentation.viewmodel.auth.AuthViewModel
import com.andrefillype.iwanttobelieve.presentation.viewmodel.feed.FeedViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    appContainer: AppContainer,
    onNavigateToFeed: () -> Unit = {},
    onNavigateToCreatePost: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    val authViewModel: AuthViewModel = appViewModel(
        modelClass = AuthViewModel::class.java,
        appContainer = appContainer
    )

    val feedViewModel: FeedViewModel = appViewModel(
        modelClass = FeedViewModel::class.java,
        appContainer = appContainer
    )

    val authUiState by authViewModel.uiState.collectAsStateWithLifecycle()
    val feedUiState by feedViewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    // Filtrar apenas os posts do usuário logado
    val userPosts = feedUiState.posts.filter { post ->
        post.authorId == authUiState.currentUser?.uid
    }

    // Carregar posts quando entrar na tela
    LaunchedEffect(Unit) {
        feedViewModel.loadPostsIfSignedIn()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Perfil",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                actions = {
                    TextButton(
                        onClick = {
                            scope.launch {
                                authViewModel.signOut()
                                onNavigateToLogin()
                            }
                        }
                    ) {
                        Text(
                            text = "Sair",
                            color = Color.Red,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White
            ) {
                NavigationBarItem(
                    icon = {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = "Feed"
                        )
                    },
                    label = { Text("Feed") },
                    selected = false,
                    onClick = onNavigateToFeed,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        unselectedIconColor = Color.Gray,
                        selectedTextColor = Color.Black,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color.Yellow
                    )
                )

                NavigationBarItem(
                    icon = {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Perfil"
                        )
                    },
                    label = { Text("Perfil") },
                    selected = true,
                    onClick = { },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        unselectedIconColor = Color.Gray,
                        selectedTextColor = Color.Black,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color.Yellow
                    )
                )
            }
        },
        containerColor = Color.White
    ) { paddingValues ->

        PullToRefreshBox(
            isRefreshing = feedUiState.isRefreshing,
            onRefresh = {
                feedViewModel.refreshPostsIfSignedIn()
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header com informações do usuário centralizado
                item {
                    ProfileHeader(user = authUiState.currentUser)
                }

                if (feedUiState.isLoading && userPosts.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(color = Color.Black)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Carregando posts...",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                } else if (userPosts.isEmpty() && !feedUiState.isLoading) {
                    item {
                        EmptyProfileMessage()
                    }
                } else {
                    items(
                        items = userPosts,
                        key = { post -> post.uid }
                    ) { post ->
                        PostItem(
                            post = post,
                            currentUser = authUiState.currentUser,
                            onLikeClick = {
                                authUiState.currentUser?.uid?.let { currentUserId ->
                                    feedViewModel.toggleLike(post, currentUserId)
                                }
                            },
                            onCommentClick = { /* Handle comment */ },
                            onShareClick = { /* Handle share */ },
                            onMoreClick = {
                                feedViewModel.deletePost(post.uid)
                            }
                        )
                    }
                }

                // Error message
                feedUiState.error?.let { error ->
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Red.copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "❌ Ops! Algo deu errado",
                                    color = Color.Red,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = error,
                                    color = Color.Red,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = {
                                        feedViewModel.clearError()
                                        feedViewModel.loadPostsIfSignedIn()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Red
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        "Tentar Novamente",
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileHeader(user: User?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Yellow.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.Yellow),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user?.name?.firstOrNull()?.uppercase() ?: "?",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nome
            Text(
                text = user?.name ?: "Nome não disponível",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Email
            Text(
                text = user?.email ?: "Email não disponível",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            // Data de criação
            user?.createdAt?.let { createdAt ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Membro desde ${formatTimestamp(createdAt)}",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun EmptyProfileMessage() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "📸",
            fontSize = 64.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Nenhum post ainda",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Compartilhe seus primeiros momentos!",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "✨ 👽 ✨",
            fontSize = 32.sp
        )
    }
}
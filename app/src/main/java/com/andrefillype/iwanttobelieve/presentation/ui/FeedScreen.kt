package com.andrefillype.iwanttobelieve.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.andrefillype.iwanttobelieve.di.module.AppContainer
import com.andrefillype.iwanttobelieve.domain.model.Post
import com.andrefillype.iwanttobelieve.domain.model.User
import com.andrefillype.iwanttobelieve.domain.usecase.auth.GetCurrentUserUseCase
import com.andrefillype.iwanttobelieve.presentation.viewmodel.appViewModel
import com.andrefillype.iwanttobelieve.presentation.viewmodel.auth.AuthViewModel
import com.andrefillype.iwanttobelieve.presentation.viewmodel.feed.FeedViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    appContainer: AppContainer,
    onNavigateToProfile: () -> Unit = {},
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

    // Carregar posts quando entrar na tela
    LaunchedEffect(Unit) {
        feedViewModel.loadPostsIfSignedIn()
    }

    // Remova essas linhas antigas:
    // val pullToRefreshState = rememberPullToRefreshState()
    // LaunchedEffect(pullToRefreshState.isRefreshing) { ... }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    Color.Yellow,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "\uD83D\uDC7D",
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "I Want to Believe",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Perfil",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreatePost,
                containerColor = Color.Yellow,
                contentColor = Color.Black,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Nova publicação",
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        containerColor = Color.White
    ) { paddingValues ->

        // Versão corrigida do PullToRefreshBox
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
                if (feedUiState.isLoading && feedUiState.posts.isEmpty()) {
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
                } else if (feedUiState.posts.isEmpty() && !feedUiState.isLoading) {
                    item {
                        EmptyFeedMessage()
                    }
                } else {
                    items(
                        items = feedUiState.posts,
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
fun PostItem(
    post: Post,
    currentUser: User?,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onShareClick: () -> Unit,
    onMoreClick: () -> Unit
) {

    val liked = currentUser?.uid?.let { post.likes.contains(it) } ?: false

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile picture placeholder
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Gray.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = post.authorName.firstOrNull()?.uppercase() ?: "?",
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = post.authorName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                        Text(
                            text = formatTimestamp(post.createdAt),
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                if (post.authorId == currentUser?.uid) {
                    IconButton(onClick = { onMoreClick() }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Excluir Post",
                            tint = Color.Red
                        )
                    }
                }
            }

            // Image
            AsyncImage(
                model = post.imageUrl,
                contentDescription = "Post image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentScale = ContentScale.Crop,
                onError = {
                    // Fallback para erro de imagem
                }
            )

            // Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { onLikeClick() }
                            .padding(4.dp)
                    ) {
                        Icon(
                            imageVector = if (liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (liked) Color.Red else Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${post.likes.size}", // Placeholder - substituir por post.likesCount
                            fontSize = 14.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))
                }
            }



            // Description with username
            if (post.description.isNotEmpty()) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        ) {
                            append("${post.authorName}: ")
                        }
                        withStyle(
                            style = SpanStyle(
                                color = Color.Black
                            )
                        ) {
                            append(post.description)
                        }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun EmptyFeedMessage() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "👽",
            fontSize = 64.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Ainda não há posts",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Seja o primeiro a compartilhar algo!",
            fontSize = 14.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "🛸 ✨ 👾",
            fontSize = 32.sp
        )
    }
}

fun formatTimestamp(timestamp: Date): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp.time

    return when {
        diff < 60_000 -> "Agora"
        diff < 3600_000 -> "${diff / 60_000}min"
        diff < 86_400_000 -> "${diff / 3600_000}h"
        diff < 604_800_000 -> "${diff / 86_400_000}d"
        else -> {
            val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
            sdf.format(timestamp)
        }
    }
}
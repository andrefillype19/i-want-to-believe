package com.andrefillype.iwanttobelieve.presentation.viewmodel.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrefillype.iwanttobelieve.domain.model.Post
import com.andrefillype.iwanttobelieve.domain.usecase.auth.GetCurrentUserUseCase
import com.andrefillype.iwanttobelieve.domain.usecase.auth.SignOutUseCase
import com.andrefillype.iwanttobelieve.domain.usecase.post.DeletePostUseCase
import com.andrefillype.iwanttobelieve.domain.usecase.post.GetAllPostsUseCase
import com.andrefillype.iwanttobelieve.domain.usecase.post.ToggleLikeUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FeedUiState(
    val isLoading: Boolean = false,
    val posts: List<Post> = emptyList(),
    val error: String? = null,
    val isRefreshing: Boolean = false
)

class FeedViewModel(
    private val getAllPostsUseCase: GetAllPostsUseCase,
    private val deletePostUseCase: DeletePostUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val toggleLikeUseCase: ToggleLikeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    private var postsJob: Job? = null

    init {
        // Inicialização limpa - não carrega posts automaticamente
    }

    fun loadPostsIfSignedIn() {
        // Evita múltiplas execuções se já está carregando
        if (_uiState.value.isLoading) {
            println("🔄 FeedViewModel: Já está carregando, ignorando...")
            return
        }

        println("🚀 FeedViewModel: Iniciando loadPostsIfSignedIn...")

        // Cancela job anterior se existir
        postsJob?.cancel()

        postsJob = viewModelScope.launch {
            println("⏳ FeedViewModel: Verificando usuário atual...")
            val currentUser = getCurrentUserUseCase().getOrNull()
            println("👤 FeedViewModel: Usuário atual: $currentUser")

            if (currentUser == null) {
                println("❌ FeedViewModel: Usuário não autenticado")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Usuário não autenticado"
                )
                return@launch
            }

            println("📱 FeedViewModel: Definindo loading = true")
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                println("📡 FeedViewModel: Chamando getAllPostsUseCase...")
                getAllPostsUseCase().collectLatest { posts ->
                    println("📦 FeedViewModel: Recebidos ${posts.size} posts: $posts")
                    _uiState.value = _uiState.value.copy(
                        posts = posts,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                println("💥 FeedViewModel: Erro: ${e.message}")
                if (!e.isCoroutineCancellation()) {
                    if (e.message?.contains("PERMISSION_DENIED") == true) {
                        signOutUseCase()
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Erro ao carregar posts"
                    )
                }
            }
        }
    }

    fun refreshPostsIfSignedIn() {
        viewModelScope.launch {
            val currentUser = getCurrentUserUseCase().getOrNull()
            if (currentUser != null) {
                refreshPosts()
            }
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            try {
                deletePostUseCase(postId) // 🔥 usa o use case
                // Atualiza lista depois da exclusão
                loadPostsIfSignedIn()
            } catch (e: Exception) {
                if (!e.isCoroutineCancellation()) {
                    _uiState.value = _uiState.value.copy(
                        error = e.message ?: "Erro ao excluir post"
                    )
                }
            }
        }
    }

    fun toggleLike(post: Post, currentUserId: String) {
        viewModelScope.launch {
            val alreadyLiked = post.likes.contains(currentUserId)

            // --- Atualização otimista ---
            _uiState.update { state ->
                val updatedPosts = state.posts.map { p ->
                    if (p.uid == post.uid) {
                        val newLikes = if (alreadyLiked) {
                            p.likes - currentUserId
                        } else {
                            p.likes + currentUserId
                        }
                        p.copy(likes = newLikes)
                    } else p
                }
                state.copy(posts = updatedPosts)
            }

            // --- Chamada real ao Firestore ---
            val result = toggleLikeUseCase(post.uid, currentUserId)

            if (result.isFailure) {
                // Se falhar, reverte para o estado original
                _uiState.update { state ->
                    val revertedPosts = state.posts.map { p ->
                        if (p.uid == post.uid) post else p
                    }
                    state.copy(
                        posts = revertedPosts,
                        error = result.exceptionOrNull()?.message ?: "Erro ao curtir post"
                    )
                }
            }
        }
    }




    private fun refreshPosts() {
        // Cancela job anterior se existir
        postsJob?.cancel()

        postsJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isRefreshing = true,
                error = null
            )

            try {
                getAllPostsUseCase().collect { posts ->
                    _uiState.value = _uiState.value.copy(
                        isRefreshing = false,
                        posts = posts,
                        error = null
                    )
                }
            } catch (e: Exception) {
                if (!e.isCoroutineCancellation()) {
                    _uiState.value = _uiState.value.copy(
                        isRefreshing = false,
                        error = e.message ?: "Erro ao atualizar posts"
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    override fun onCleared() {
        super.onCleared()
        postsJob?.cancel()
    }

    private fun Exception.isCoroutineCancellation(): Boolean {
        return this is kotlinx.coroutines.CancellationException
    }
}
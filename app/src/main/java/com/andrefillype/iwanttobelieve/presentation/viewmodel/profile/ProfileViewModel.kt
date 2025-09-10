package com.andrefillype.iwanttobelieve.presentation.viewmodel.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrefillype.iwanttobelieve.domain.model.User
import com.andrefillype.iwanttobelieve.domain.usecase.auth.GetCurrentUserUseCase
import com.andrefillype.iwanttobelieve.domain.usecase.auth.SignOutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val currentUser: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class ProfileViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState(isLoading = true))
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                val user = getCurrentUserUseCase().getOrNull()
                _uiState.value = ProfileUiState(
                    currentUser = user,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = ProfileUiState(
                    currentUser = null,
                    isLoading = false,
                    error = e.message ?: "Erro ao carregar usuário"
                )
            }
        }
    }

    fun signOut(onSignedOut: () -> Unit) {
        viewModelScope.launch {
            try {
                signOutUseCase()
                _uiState.value = ProfileUiState(currentUser = null, isLoading = false)
                onSignedOut()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Erro ao sair"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

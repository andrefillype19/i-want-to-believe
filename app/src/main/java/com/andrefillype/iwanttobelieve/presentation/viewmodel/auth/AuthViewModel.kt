package com.andrefillype.iwanttobelieve.presentation.viewmodel.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrefillype.iwanttobelieve.domain.model.User
import com.andrefillype.iwanttobelieve.domain.usecase.auth.SignInUseCase
import com.andrefillype.iwanttobelieve.domain.usecase.auth.SignUpUseCase
import com.andrefillype.iwanttobelieve.domain.usecase.auth.GetCurrentUserUseCase
import com.andrefillype.iwanttobelieve.domain.usecase.auth.SignOutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSignedIn: Boolean = false,
    val currentUser: User? = null,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val isCheckingAuth: Boolean = true // <- novo
)

class AuthViewModel(
    private val signInUseCase: SignInUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkCurrentUser()
    }

    fun checkCurrentUser() {
        viewModelScope.launch {
            try {
                getCurrentUserUseCase().fold(
                    onSuccess = { user ->
                        Log.d("AuthViewModel", "Usuário atual: $user")
                        _uiState.value = _uiState.value.copy(
                            isSignedIn = user != null,
                            currentUser = user,
                            isCheckingAuth = false // terminou
                        )
                    },
                    onFailure = { e ->
                        Log.e("AuthViewModel", "Erro ao obter usuário", e)
                        _uiState.value = _uiState.value.copy(
                            isSignedIn = false,
                            currentUser = null,
                            isCheckingAuth = false // terminou
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSignedIn = false,
                    currentUser = null,
                    isCheckingAuth = false // terminou
                )
            }
        }
    }


    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(
                error = "Email e senha são obrigatórios"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                isSuccess = false
            )

            signInUseCase(email, password).fold(
                onSuccess = {
                    val user = getCurrentUserUseCase().getOrNull()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSignedIn = user != null,
                        currentUser = user,
                        isSuccess = true
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Erro ao fazer login",
                        isSuccess = false
                    )
                }
            )
        }
    }

    fun signUp(name: String, email: String, password: String) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(
                error = "Todos os campos são obrigatórios"
            )
            return
        }

        if (password.length < 6) {
            _uiState.value = _uiState.value.copy(
                error = "A senha deve ter pelo menos 6 caracteres"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                isSuccess = false
            )

            signUpUseCase(name, email, password).fold(
                onSuccess = {
                    // depois de criar a conta, buscamos o usuário logado
                    val user = getCurrentUserUseCase().getOrNull()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSignedIn = user != null,
                        currentUser = user,
                        isSuccess = true
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Erro ao criar conta",
                        isSuccess = false
                    )
                }
            )
        }
    }


    fun signOut() {
        viewModelScope.launch {
            try {
                signOutUseCase()
                _uiState.value = AuthUiState() // Reset to initial state
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

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(isSuccess = false)
    }
}
package com.andrefillype.iwanttobelieve.domain.usecase.auth

import com.andrefillype.iwanttobelieve.domain.repository.AuthRepository
import android.util.Patterns

class SignUpUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        name: String,
        email: String,
        password: String
    ): Result<Unit> {
        if (name.isBlank()) {
            return Result.failure(Exception("Nome não pode estar vazio"))
        }

        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.failure(Exception("Email inválido"))
        }

        if (password.length < 8) {
            return Result.failure(Exception("Senha deve ter pelo menos 6 caracteres"))
        }

        return authRepository.signUp(name, email, password)
    }
}
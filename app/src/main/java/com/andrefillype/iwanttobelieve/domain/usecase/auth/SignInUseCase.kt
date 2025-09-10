package com.andrefillype.iwanttobelieve.domain.usecase.auth

import com.andrefillype.iwanttobelieve.domain.repository.AuthRepository

class SignInUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String
    ): Result<Unit> {
        if (email.isBlank()) {
            return Result.failure(Exception("Email não pode estar vazio"))
        }

        if (password.isBlank()) {
            return Result.failure(Exception("Senha não pode estar vazia"))
        }

        return authRepository.signIn(email, password)
    }
}
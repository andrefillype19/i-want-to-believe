package com.andrefillype.iwanttobelieve.domain.usecase.auth

import com.andrefillype.iwanttobelieve.domain.repository.AuthRepository

class SignOutUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.signOut()
    }
}

package com.andrefillype.iwanttobelieve.domain.usecase.auth

import com.andrefillype.iwanttobelieve.data.repository.AuthRepositoryImpl
import com.andrefillype.iwanttobelieve.domain.repository.AuthRepository

class CheckUserActiveUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Boolean {
        val firebaseUser = authRepository.getCurrentFirebaseUser() ?: return false
        return authRepository.isUserActive(firebaseUser)
    }
}



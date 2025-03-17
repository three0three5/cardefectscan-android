package ru.hse.cardefectscan.domain.usecase

import ru.hse.cardefectscan.domain.repository.AuthRepository

class AuthUseCase(
    private val authRepository: AuthRepository,
) {
    fun isAuthenticated(): Boolean {
        val token = authRepository.getToken() ?: return false
        val expiration = token.expiresAt
        return System.currentTimeMillis() < expiration
    }
}
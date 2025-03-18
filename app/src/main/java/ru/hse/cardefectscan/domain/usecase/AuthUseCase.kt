package ru.hse.cardefectscan.domain.usecase

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import ru.hse.cardefectscan.domain.repository.AuthRepository
import ru.hse.generated.apis.AuthApi
import ru.hse.generated.models.LoginRequest

class AuthUseCase(
    private val authRepository: AuthRepository,
    private val authClient: AuthApi,
) {
    fun isAuthenticated(): Boolean {
        val token = authRepository.getRefreshToken() ?: return false
        val expiration = token.cookie.expiresAt
        Log.d("AuthUseCase", "returning ${System.currentTimeMillis() < expiration} for isAuthenticated")
        return System.currentTimeMillis() < expiration
    }

    suspend fun logout() {
        withContext(Dispatchers.IO) {
            Log.d("AuthUseCase", "Trying to logout")
            authClient.apiV1AuthLogoutPost()
        }
        authRepository.clear()
    }

    suspend fun login(login: String, password: String) {
        val tokenResponse = withContext(Dispatchers.IO) {
            authClient.apiV1AuthLoginPost(
                LoginRequest(login, password)
            )
        }
        Log.d("AuthUseCase", "received response $tokenResponse")
        authRepository.jwtToken = tokenResponse.accessToken
    }

    fun refresh() {
        Log.d("AuthUseCase", "Refreshing token")
        val tokenResponse = authClient.apiV1AuthRefreshPost()
        Log.d("AuthUseCase", "Received ${tokenResponse.accessToken}")
        authRepository.jwtToken = tokenResponse.accessToken
    }
}
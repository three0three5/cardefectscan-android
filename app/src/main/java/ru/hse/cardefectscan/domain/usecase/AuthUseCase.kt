package ru.hse.cardefectscan.domain.usecase

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.hse.cardefectscan.domain.repository.AuthRepository
import ru.hse.cardefectscan.presentation.exception.PasswordsNotMatchException
import ru.hse.generated.apis.AuthApi
import ru.hse.generated.models.LoginRequest
import ru.hse.generated.models.SignupRequest

class AuthUseCase(
    private val authRepository: AuthRepository,
    private val authApi: AuthApi,
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
            authApi.apiV1AuthLogoutPost()
        }
        authRepository.clear()
    }

    suspend fun login(login: String, password: String) {
        val tokenResponse = withContext(Dispatchers.IO) {
            authApi.apiV1AuthLoginPost(
                LoginRequest(login, password)
            )
        }
        Log.d("AuthUseCase", "received response $tokenResponse")
        authRepository.jwtToken = tokenResponse.accessToken
    }

   suspend fun refresh() {
        Log.d("AuthUseCase", "Refreshing token")
        val tokenResponse = withContext(Dispatchers.IO) {
            authApi.apiV1AuthRefreshPost()
        }
        Log.d("AuthUseCase", "Received ${tokenResponse.accessToken}")
        authRepository.jwtToken = tokenResponse.accessToken
    }

    suspend fun signup(login: String, password: String, additionalPassword: String) {
        if (password != additionalPassword) throw PasswordsNotMatchException()
        val tokenResponse = withContext(Dispatchers.IO) {
            authApi.apiV1AuthSignupPost(SignupRequest(
                username = login,
                password = password,
            ))
        }
        Log.d("AuthUseCase", "received response $tokenResponse")
        authRepository.jwtToken = tokenResponse.accessToken
    }
}
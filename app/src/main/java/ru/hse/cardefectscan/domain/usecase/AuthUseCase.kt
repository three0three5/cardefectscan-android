package ru.hse.cardefectscan.domain.usecase

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.hse.cardefectscan.domain.repository.AuthRepository
import ru.hse.cardefectscan.presentation.exception.LoginTooShortException
import ru.hse.cardefectscan.presentation.exception.PasswordTooShortException
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
        Log.d(
            "AuthUseCase",
            "returning ${System.currentTimeMillis() < expiration} for isAuthenticated"
        )
        return System.currentTimeMillis() < expiration
    }

    suspend fun logout() {
        withContext(Dispatchers.IO) {
            Log.d("AuthUseCase", "Trying to logout")
            authApi.apiV1AuthLogoutPost()
        }
        Log.d("AuthUseCase", "Logout processed, left IO coroutine")
        authRepository.clear()
    }

    suspend fun login(login: String, password: String) {
        validate(login, password)
        val tokenResponse = withContext(Dispatchers.IO) {
            Log.d("AuthUseCase", "Entered IO context")
            authApi.apiV1AuthLoginPost(
                LoginRequest(login, password)
            )
        }
        Log.d("AuthUseCase", "received response $tokenResponse")
        authRepository.jwtToken = tokenResponse.accessToken
        Log.d("AuthUseCase", "Now authRepository.jwtToken value is ${authRepository.jwtToken}")
    }

    suspend fun refresh() {
        Log.d("AuthUseCase", "Refreshing token")
        val tokenResponse = withContext(Dispatchers.IO) {
            authApi.apiV1AuthRefreshPost()
        }
        Log.d("AuthUseCase", "Received ${tokenResponse.accessToken}")
        authRepository.jwtToken = tokenResponse.accessToken
        Log.d("AuthUseCase", "Now authRepository.jwtToken value is ${authRepository.jwtToken}")
    }

    suspend fun signup(login: String, password: String, additionalPassword: String) {
        validate(login, password, additionalPassword)
        val tokenResponse = withContext(Dispatchers.IO) {
            authApi.apiV1AuthSignupPost(
                SignupRequest(
                    username = login,
                    password = password,
                )
            )
        }
        Log.d("AuthUseCase", "received response $tokenResponse")
        authRepository.jwtToken = tokenResponse.accessToken
        Log.d("AuthUseCase", "Now authRepository.jwtToken value is ${authRepository.jwtToken}")
    }

    private fun validate(
        login: String,
        password: String,
        additionalPassword: String? = null,
    ) {
        if (login.length < 6) throw LoginTooShortException()
        if (password.length < 6) throw PasswordTooShortException()
        if (additionalPassword != null && password != additionalPassword) throw PasswordsNotMatchException()
    }
}
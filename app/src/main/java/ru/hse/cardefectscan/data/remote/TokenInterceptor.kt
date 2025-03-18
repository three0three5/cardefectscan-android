package ru.hse.cardefectscan.data.remote

import android.util.Log
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import ru.hse.cardefectscan.domain.repository.AuthRepository
import ru.hse.cardefectscan.domain.usecase.AuthUseCase
import javax.inject.Inject

class TokenInterceptor @Inject constructor(
    private val authRepository: AuthRepository,
    private val authUseCase: AuthUseCase,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = makeRequest(chain, authRepository.jwtToken)
        if (response.code != 401) return response
        runBlocking {
            Log.d("TokenInterceptor", "try to refresh in runBlocking")
            authUseCase.refresh()
        }
        return makeRequest(chain, authRepository.jwtToken)
    }

    private fun makeRequest(chain: Interceptor.Chain, token: String): Response {
        Log.d("TokenInterceptor", "Request pass")
        val header = BEARER_PREFIX + token
        val request = chain.request().newBuilder()
            .addHeader(AUTHORIZATION_HEADER, header)
            .build()
        return chain.proceed(request).also {
            Log.d("TokenInterceptor", "Response: $it")
        }
    }

    companion object {
        const val BEARER_PREFIX = "Bearer "
        const val AUTHORIZATION_HEADER = "Authorization"
    }
}
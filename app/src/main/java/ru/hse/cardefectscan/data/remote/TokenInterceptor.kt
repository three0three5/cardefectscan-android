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
        val token = authRepository.jwtToken
        var response = makeRequest(chain, token)

        if (response.code == 401) {
            authRepository.jwtToken = ""
            response.close()
            synchronized(this) {
                if (authRepository.jwtToken == "") {
                    runBlocking {
                        Log.d("TokenInterceptor", "Refreshing token")
                        authUseCase.refresh()
                    }
                }
            }
            response = makeRequest(chain, authRepository.jwtToken)
        }
        return response
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
        private const val BEARER_PREFIX = "Bearer "
        private const val AUTHORIZATION_HEADER = "Authorization"
    }
}

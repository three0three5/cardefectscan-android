package ru.hse.cardefectscan.utils

import okhttp3.Response
import ru.hse.generated.infrastructure.ClientException
import ru.hse.generated.infrastructure.ServerException

object OkHttpUtils {
    fun checkApiResponse(response: Response): Response {
        return when (response.code) {
            in 200..299 -> response
            in 400..499 -> throw ClientException(
                "Client error: ${response.code} ${response.message}",
                response.code,
            )
            in 500..599 -> throw ServerException(
                "Server error: ${response.code} ${response.message}",
                response.code,
            )
            else -> throw ClientException(
                "Unknown error: ${response.code} ${response.message}",
                response.code,
            )
        }
    }
}
package ru.hse.cardefectscan.data.remote

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import ru.hse.cardefectscan.presentation.exception.CommonException
import ru.hse.cardefectscan.presentation.exception.ServerUnavailableException
import ru.hse.cardefectscan.utils.NETWORK_ISSUES
import java.io.IOException
import java.net.SocketTimeoutException

class ExceptionHandlerInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        Log.d("ExceptionHandlerInterceptor", "Intercepting request")
        val result = runCatching {
            chain.proceed(chain.request())
        }
        if (result.isSuccess) return result.getOrNull()!!
        val throwable = result.exceptionOrNull()!!
        when (throwable) {
            is SocketTimeoutException -> throw ServerUnavailableException(NETWORK_ISSUES)
            is IOException -> throw mapIoException(throwable)
            is IllegalStateException -> throw CommonException(throwable.message)
        }
        Log.e("ExceptionHandlerInterceptor", "Unhandled exception: $throwable")
        throw throwable
    }

    private fun mapIoException(e: IOException): CommonException {
        var message = e.message
        if (message?.contains("Failed to connect") == true) {
            message = NETWORK_ISSUES
        }
        return CommonException(message)
    }
}
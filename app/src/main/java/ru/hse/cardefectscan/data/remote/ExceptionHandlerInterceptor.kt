package ru.hse.cardefectscan.data.remote

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
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
            is SocketTimeoutException -> throw IOException(NETWORK_ISSUES)
            is IOException -> throw mapIoException(throwable)
            is IllegalStateException -> throw IOException(throwable.message)
        }
        Log.e("ExceptionHandlerInterceptor", "Unhandled exception: $throwable")
        throw throwable
    }

    private fun mapIoException(e: IOException): IOException {
        if (e.message?.contains("Canceled") == true) {
            Log.i("ExceptionHandlerInterceptor", "Ignore canceled exception")
            throw e
        }
        Log.e("ExceptionHandlerInterceptor", "error: ", e)
        var message = e.message
        if (message?.contains("Failed to connect") == true) {
            message = NETWORK_ISSUES
        }
        return IOException(message)
    }
}
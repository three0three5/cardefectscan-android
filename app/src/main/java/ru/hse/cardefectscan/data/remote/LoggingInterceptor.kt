package ru.hse.cardefectscan.data.remote

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class LoggingInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        Log.d("LoggingInterceptor", "Proceeding request ${chain.request()}")
        val result = runCatching {
            chain.proceed(chain.request())
        }
        Log.d("LoggingInterceptor", "Result success: ${result.isSuccess}")
        if (!result.isSuccess) throw result.exceptionOrNull()!!
        val response = result.getOrNull()!!
        Log.d("LoggingInterceptor", "Response is $response")
        return response
    }
}
package ru.hse.cardefectscan.data.remote

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class RetryInterceptor(
    private val maxRetries: Int = 3,
    private val delayMillis: Long = 500
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var attempt = 0
        var exception: IOException? = null

        while (attempt < maxRetries) {
            if (attempt != 0) {
                Log.d("RetryInterceptor", "Retrying attempt #$attempt")
            }
            try {
                return chain.proceed(chain.request())
            } catch (e: IOException) {
                Log.e("RetryInterceptor", "Caught an exception to retry: $e")
                exception = e
                attempt++
                if (attempt < maxRetries) {
                    Thread.sleep(delayMillis)
                }
            }
        }
        throw exception ?: IOException("Unknown IOException")
    }
}

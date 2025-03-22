package ru.hse.cardefectscan.data.remote

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import java.io.IOException

class MinioClient(
    private val client: OkHttpClient,
    private val context: Context,
) {
    suspend fun putImage(imageUri: Uri, uploadLink: String) = withContext(Dispatchers.IO) {
        val contentResolver = context.contentResolver
        val mediaType = contentResolver.getType(imageUri)?.toMediaTypeOrNull() ?: "application/octet-stream".toMediaTypeOrNull()
        Log.d("MinioClient", "Media type: $mediaType")
        val inputStream = contentResolver.openInputStream(imageUri) ?: throw IOException("Failed to open InputStream")
        inputStream.use {
            val requestBody = object : RequestBody() {
                override fun contentType(): MediaType? = mediaType

                override fun writeTo(sink: BufferedSink) {
                    it.source().use { source ->
                        sink.writeAll(source)
                    }
                }
            }
            val request = Request.Builder()
                .url(uploadLink)
                .put(requestBody)
                .build()
            runCatching {
                client.newCall(request).execute()
            }.onFailure { ex ->
                Log.e("MinioClient", "An error occurred in put request: $ex")
                throw ex
            }
        }
    }
}
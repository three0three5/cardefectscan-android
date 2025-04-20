package ru.hse.cardefectscan.data.remote

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import coil3.BitmapImage
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import ru.hse.cardefectscan.utils.OkHttpUtils
import java.io.IOException

class MinioClient(
    private val defaultClient: OkHttpClient,
    private val authenticatedClient: OkHttpClient,
    private val context: Context,
    private val imageLoader: ImageLoader,
) {
    suspend fun putImage(imageUri: Uri, uploadLink: String) = withContext(Dispatchers.IO) {
        val contentResolver = context.contentResolver
        val mediaType = contentResolver.getType(imageUri)?.toMediaTypeOrNull()
            ?: "application/octet-stream".toMediaTypeOrNull()
        Log.d("MinioClient", "Media type: $mediaType")
        val inputStream = contentResolver.openInputStream(imageUri)
            ?: throw IOException("Failed to open InputStream")
        val requestBody = object : RequestBody() {
            override fun contentType(): MediaType? = mediaType

            override fun writeTo(sink: BufferedSink) {
                inputStream.source().use { source ->
                    sink.writeAll(source)
                }
            }
        }
        val request = Request.Builder()
            .url(uploadLink)
            .put(requestBody)
            .build()
        runCatching {
            val response = defaultClient.newCall(request).execute()
            OkHttpUtils.checkApiResponse(response)
        }.onFailure { ex ->
            Log.e("MinioClient", "An error occurred in put request: $ex")
            throw ex
        }
    }

    suspend fun loadBitmapWithMetadata(imageLink: String): Pair<Bitmap, String>? {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder().url(imageLink).build()
                val response = authenticatedClient.newCall(request).execute()

                if (!response.isSuccessful) return@withContext null
                val body = response.body ?: return@withContext null

                val bitmap = BitmapFactory.decodeStream(body.byteStream())
                val metadata = response.headers["x-amz-meta-json-data"] ?: run {
                    Log.e("RequestsUseCase", "Metadata not found in response headers")
                    return@withContext null
                }

                Pair(bitmap, metadata)
            } catch (e: Exception) {
                Log.e("RequestsUseCase", "Error loading image: ${e.message}")
                null
            }
        }
    }


    suspend fun loadBitmap(imageLink: String): Bitmap? {
        val request = ImageRequest.Builder(context)
            .data(imageLink)
            .build()

        return withContext(Dispatchers.IO) {
            val result = imageLoader.execute(request)
            if (result is SuccessResult) {
                val drawable = result.image
                if (drawable is BitmapImage) {
                    drawable.bitmap
                } else {
                    Log.d(
                        "RequestsUseCase",
                        "Result with imageLink $imageLink is not a BitmapImage"
                    )
                    null
                }
            } else {
                Log.d("RequestsUseCase", "Result with imageLink $imageLink is not a SuccessResult")
                null
            }
        }
    }
}
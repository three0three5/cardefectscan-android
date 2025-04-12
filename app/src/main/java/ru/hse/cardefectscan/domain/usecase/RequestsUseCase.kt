package ru.hse.cardefectscan.domain.usecase

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import ru.hse.cardefectscan.domain.repository.RequestsPagingSource
import ru.hse.generated.apis.RequestsApi
import ru.hse.generated.models.ImageRequestDetailed
import ru.hse.generated.models.ImageRequestStatus
import ru.hse.generated.models.ResultList
import java.time.OffsetDateTime

class RequestsUseCase(
    private val requestsApi: RequestsApi,
    private val imageLoader: ImageLoader,
    private val okHttpClient: OkHttpClient,
    private val context: Context,
    val pagingSource: RequestsPagingSource,
) {

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val resultListAdapter = moshi.adapter(ResultList::class.java)

    suspend fun getOriginalAndRenderedDrawable(imageId: String) = coroutineScope {
        val info = detailedInfo(imageId)

        val originalJob = async {
            loadBitmap(imageLink = info.originalImageDownloadLink)
        }

        val renderedJob = async {
            info.resultImageDownloadLink?.let { link ->
                loadBitmapWithMetadata(link)?.let { (bitmap, metadataJson) ->
                    Pair(bitmap.asImageBitmap(), parseMetadata(metadataJson))
                }
            }
        }

        val originalBitmap = originalJob.await()
        val rendered = renderedJob.await()

        Log.d("RequestsUseCase", "Original loaded: ${originalBitmap != null}")
        Log.d("RequestsUseCase", "Rendered loaded: ${rendered != null}")

        return@coroutineScope info.toResult(
            original = originalBitmap?.asImageBitmap(),
            result = rendered
        )
    }

    private suspend fun loadBitmap(imageLink: String): Bitmap? {
        val request = ImageRequest.Builder(context)
            .data(imageLink)
            .build()

        return withContext(Dispatchers.IO) {
            val result = imageLoader.execute(request)
            if (result is SuccessResult) {
                val drawable = result.image
                if (drawable is BitmapDrawable) {
                    drawable.bitmap
                } else null
            } else null
        }
    }

    private suspend fun loadBitmapWithMetadata(imageLink: String): Pair<Bitmap, String>? {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder().url(imageLink).build()
                val response = okHttpClient.newCall(request).execute()

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

    private suspend fun detailedInfo(imageId: String): ImageRequestDetailed {
        return withContext(Dispatchers.IO) {
            requestsApi.apiV1RequestsImageIdGet(imageId)
        }
    }

    private fun parseMetadata(json: String): ResultList =
        resultListAdapter.fromJson(json)
            ?: throw IllegalArgumentException("Невозможно распарсить json: $json")
}

data class ProcessedResult(
    val imageId: String,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
    val status: ImageRequestStatus,
    val original: ImageBitmap? = null,
    val result: Pair<ImageBitmap, ResultList>? = null
)

fun ImageRequestDetailed.toResult(
    original: ImageBitmap?,
    result: Pair<ImageBitmap, ResultList>?,
): ProcessedResult = ProcessedResult(
    imageId = imageId,
    createdAt = createdAt,
    updatedAt = updatedAt,
    status = status,
    original = original,
    result = result,
)

package ru.hse.cardefectscan.domain.usecase

import android.graphics.Bitmap
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.hse.cardefectscan.data.local.ResultDiskCache
import ru.hse.cardefectscan.data.remote.MinioClient
import ru.hse.cardefectscan.domain.repository.RequestsPagingSource
import ru.hse.generated.apis.RequestsApi
import ru.hse.generated.models.ImageRequestDetailed
import ru.hse.generated.models.ImageRequestStatus
import java.time.OffsetDateTime

private val FINAL_STATUSES = listOf(
    ImageRequestStatus.DONE,
    ImageRequestStatus.FAILED,
)

class RequestsUseCase(
    private val resultDiskCache: ResultDiskCache,
    private val requestsApi: RequestsApi,
    private val minioClient: MinioClient,
    val pagingSource: RequestsPagingSource,
) {
    suspend fun getOriginalAndRenderedDrawable(imageId: String) = coroutineScope {
        val cached = resultDiskCache.get(imageId)
        if (cached != null) return@coroutineScope cached

        return@coroutineScope getByCall(imageId, this)
    }

    private suspend fun getByCall(
        imageId: String,
        coroutineScope: CoroutineScope,
    ): ProcessedResult {
        val info = detailedInfo(imageId)

        val originalJob = coroutineScope.async {
            minioClient.loadBitmap(imageLink = info.originalImageDownloadLink)
        }

        val resultJob = coroutineScope.async {
            info.resultImageDownloadLink?.let { link ->
                minioClient.loadBitmap(link)
            }
        }

        val originalBitmap = originalJob.await()
        val result = resultJob.await()

        Log.d("RequestsUseCase", "Original loaded: ${originalBitmap != null}")
        Log.d("RequestsUseCase", "Result mask and legend loaded: ${result != null}")

        val finalResult = info.toResult(
            original = originalBitmap,
            result = result,
        )

        if (info.status in FINAL_STATUSES) coroutineScope.launch{
            Log.d("RequestsUseCase", "Saving result to disk cache")
            resultDiskCache.put(finalResult)
        }

        return finalResult
    }

    private suspend fun detailedInfo(imageId: String): ImageRequestDetailed {
        return withContext(Dispatchers.IO) {
            requestsApi.apiV1RequestsImageIdGet(imageId)
        }
    }
}

data class ProcessedResult(
    val imageId: String,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
    val status: ImageRequestStatus,
    val original: Bitmap? = null,
    val result: Bitmap? = null,
    val description: String? = null,
)

fun ImageRequestDetailed.toResult(
    original: Bitmap?,
    result: Bitmap?,
): ProcessedResult = ProcessedResult(
    imageId = imageId,
    createdAt = createdAt,
    updatedAt = updatedAt,
    status = status,
    original = original,
    result = result,
    description = description,
)

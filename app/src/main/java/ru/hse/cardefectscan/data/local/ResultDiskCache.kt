package ru.hse.cardefectscan.data.local

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.squareup.moshi.JsonClass
import ru.hse.cardefectscan.domain.usecase.ProcessedResult
import ru.hse.generated.infrastructure.Serializer
import ru.hse.generated.models.ImageRequestStatus
import ru.hse.generated.models.ResultList
import ru.hse.generated.models.ResultMetadata
import java.io.File
import java.time.OffsetDateTime

class ResultDiskCache(
    context: Context,
) {
    private val baseDir = File(context.cacheDir, "results").apply { mkdirs() }
    private val adapter = Serializer.moshi.adapter(CachedProcessedResult::class.java)

    fun get(imageId: String): ProcessedResult? {
        val metaFile = File(baseDir, "${imageId}.meta.json")
        if (!metaFile.exists()) return null

        return try {
            val cached = adapter.fromJson(metaFile.readText()) ?: return null

            val originalBitmap = if (cached.hasOriginal) {
                File(baseDir, "${imageId}.orig.png").takeIf { it.exists() }
                    ?.let { BitmapFactory.decodeFile(it.absolutePath) }
            } else null

            val resultBitmap = File(baseDir, "${imageId}.res.png").takeIf { it.exists() }
                ?.let { BitmapFactory.decodeFile(it.absolutePath) }

            val resultPair = if (cached.resultMetadata != null && resultBitmap != null) {
                Pair(resultBitmap, ResultList(cached.resultMetadata))
            } else null

            ProcessedResult(
                imageId = cached.imageId,
                createdAt = OffsetDateTime.parse(cached.createdAt),
                updatedAt = OffsetDateTime.parse(cached.updatedAt),
                status = cached.status,
                original = originalBitmap,
                result = resultPair
            )
        } catch (e: Exception) {
            Log.e("ResultDiskCache", "Error while loading result: ${e.message}")
            metaFile.delete()
            null
        }
    }

    fun put(result: ProcessedResult) {
        val id = result.imageId
        val now = OffsetDateTime.now().toString()

        val hasOrig = result.original != null
        if (hasOrig) {
            File(baseDir, "${id}.orig.png").outputStream().use { out ->
                result.original!!.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
        }

        val hasRes = result.result?.first != null
        if (hasRes) {
            File(baseDir, "${id}.res.png").outputStream().use { out ->
                result.result!!.first.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
        }

        val cached = CachedProcessedResult(
            imageId = id,
            createdAt = result.createdAt.toString(),
            updatedAt = now,
            status = result.status,
            hasOriginal = hasOrig,
            resultMetadata = result.result?.second?.result
        )
        File(baseDir, "${id}.meta.json").writeText(adapter.toJson(cached))
    }
}

@JsonClass(generateAdapter = true)
data class CachedProcessedResult(
    val imageId: String,
    val createdAt: String,
    val updatedAt: String,
    val status: ImageRequestStatus,
    val hasOriginal: Boolean = false,
    val resultMetadata: Map<String, ResultMetadata>? = null
)

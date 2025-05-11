package ru.hse.cardefectscan.data.local

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.squareup.moshi.JsonClass
import ru.hse.cardefectscan.domain.usecase.ProcessedResult
import ru.hse.generated.infrastructure.Serializer
import ru.hse.generated.models.ImageRequestStatus
import java.io.File
import java.time.OffsetDateTime

class ResultDiskCache(
    val context: Context,
) {
    private val adapter = Serializer.moshi.adapter(CachedProcessedResult::class.java)

    fun get(imageId: String): ProcessedResult? {
        val baseDir = File(context.cacheDir, "results").apply { mkdirs() }
        val metaFile = File(baseDir, "${imageId}.meta.json")
        if (!metaFile.exists()) return null

        return try {
            val cached = adapter.fromJson(metaFile.readText()) ?: return null

            val originalBitmap = File(baseDir, "${imageId}.orig.png").takeIf { it.exists() }
                    ?.let { BitmapFactory.decodeFile(it.absolutePath) }

            val resultBitmap = File(baseDir, "${imageId}.res.png").takeIf { it.exists() }
                ?.let { BitmapFactory.decodeFile(it.absolutePath) }

            ProcessedResult(
                imageId = cached.imageId,
                createdAt = OffsetDateTime.parse(cached.createdAt),
                updatedAt = OffsetDateTime.parse(cached.updatedAt),
                status = cached.status,
                original = originalBitmap,
                result = resultBitmap,
                description = cached.description,
            )
        } catch (e: Exception) {
            Log.e("ResultDiskCache", "Error while loading result: ${e.message}")
            metaFile.delete()
            null
        }
    }

    fun put(result: ProcessedResult) {
        val id = result.imageId
        val baseDir = File(context.cacheDir, "results").apply { mkdirs() }

        result.original?.let {
            File(baseDir, "${id}.orig.png").outputStream().use { out ->
                it.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
        }

        result.result?.let {
            File(baseDir, "${id}.res.png").outputStream().use { out ->
                it.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
        }

        val cached = CachedProcessedResult(
            imageId = id,
            createdAt = result.createdAt.toString(),
            updatedAt = result.updatedAt.toString(),
            status = result.status,
            description = result.description,
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
    val description: String? = null,
)

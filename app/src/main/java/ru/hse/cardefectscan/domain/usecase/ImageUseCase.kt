package ru.hse.cardefectscan.domain.usecase

import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.hse.cardefectscan.data.remote.MinioClient
import ru.hse.generated.apis.ImagesApi

class ImageUseCase(
    private val imagesApi: ImagesApi,
    private val minioClient: MinioClient,
) {
    suspend fun upload(imageUri: Uri) {
        val uploadLink = withContext(Dispatchers.IO) {
            imagesApi.apiV1ImagesLoadGet()
        }.link
        Log.d("ImageUseCase", "received link $uploadLink")
        minioClient.putImage(imageUri, uploadLink)
    }
}
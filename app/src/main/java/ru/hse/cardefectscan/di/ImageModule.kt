package ru.hse.cardefectscan.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import ru.hse.cardefectscan.data.remote.MinioClient
import ru.hse.cardefectscan.domain.usecase.ImageUseCase
import ru.hse.generated.apis.ImagesApi

@Module
@InstallIn(SingletonComponent::class)
class ImageModule {
    @Provides
    @Singleton
    fun provideImageUseCase(
        imagesApi: ImagesApi,
        minioClient: MinioClient,
    ): ImageUseCase = ImageUseCase(
        imagesApi,
        minioClient,
    )
}
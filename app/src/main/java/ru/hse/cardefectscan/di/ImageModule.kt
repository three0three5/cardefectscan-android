package ru.hse.cardefectscan.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.hse.cardefectscan.domain.usecase.ImageUseCase

@Module
@InstallIn(SingletonComponent::class)
class ImageModule {
    @Provides
    fun provideImageUseCase(
    ): ImageUseCase = ImageUseCase()
}
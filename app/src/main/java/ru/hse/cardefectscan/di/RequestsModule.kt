package ru.hse.cardefectscan.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.hse.cardefectscan.data.local.ResultDiskCache
import ru.hse.cardefectscan.data.remote.MinioClient
import ru.hse.cardefectscan.domain.repository.RequestsPagingSource
import ru.hse.cardefectscan.domain.usecase.RequestsUseCase
import ru.hse.cardefectscan.domain.usecase.ResultUseCase
import ru.hse.generated.apis.RequestsApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RequestsModule {
    @Provides
    @Singleton
    fun provideRequestsPagingSource(
        requestsApi: RequestsApi,
    ): RequestsPagingSource = RequestsPagingSource(
        requestsApi = requestsApi,
    )


    @Provides
    @Singleton
    fun provideResultDiskCache(
        @ApplicationContext context: Context,
    ): ResultDiskCache = ResultDiskCache(
        context = context,
    )

    @Provides
    @Singleton
    fun provideRequestsUseCase(
        requestsPagingSource: RequestsPagingSource,
        requestsApi: RequestsApi,
        resultDiskCache: ResultDiskCache,
        minioClient: MinioClient,
    ): RequestsUseCase = RequestsUseCase(
        pagingSource = requestsPagingSource,
        requestsApi = requestsApi,
        resultDiskCache = resultDiskCache,
        minioClient = minioClient,
    )

    @Provides
    @Singleton
    fun provideResultUseCase(
    ) = ResultUseCase(
    )
}
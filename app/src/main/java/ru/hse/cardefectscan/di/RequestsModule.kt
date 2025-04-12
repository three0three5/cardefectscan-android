package ru.hse.cardefectscan.di

import android.content.Context
import coil3.ImageLoader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import ru.hse.cardefectscan.domain.repository.RequestsPagingSource
import ru.hse.cardefectscan.domain.usecase.RequestsUseCase
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
    fun provideRequestsUseCase(
        requestsPagingSource: RequestsPagingSource,
        requestsApi: RequestsApi,
        @ApplicationContext context: Context,
        imageLoader: ImageLoader,
        @AuthenticatedOkHttpClient okHttpClient: OkHttpClient,
    ): RequestsUseCase = RequestsUseCase(
        pagingSource = requestsPagingSource,
        requestsApi = requestsApi,
        context = context,
        imageLoader = imageLoader,
        okHttpClient = okHttpClient,
    )
}
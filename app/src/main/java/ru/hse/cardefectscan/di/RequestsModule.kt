package ru.hse.cardefectscan.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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
    ): RequestsUseCase = RequestsUseCase(requestsPagingSource)
}
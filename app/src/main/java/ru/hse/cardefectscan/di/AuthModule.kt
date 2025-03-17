package ru.hse.cardefectscan.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.hse.cardefectscan.domain.repository.AuthRepository
import ru.hse.cardefectscan.domain.usecase.AuthUseCase

@Module
@InstallIn(SingletonComponent::class)
class AuthModule {
    @Provides
    fun provideAuthRepository(
        @ApplicationContext context: Context,
    ): AuthRepository = AuthRepository(
        context
    )

    @Provides
    fun provideAuthUseCase(
        authRepository: AuthRepository,
    ): AuthUseCase = AuthUseCase(
        authRepository
    )
}
package ru.hse.cardefectscan.presentation.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.hse.cardefectscan.domain.usecase.AuthUseCase
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
) : ViewModel() {
    fun isAuthenticated():Boolean {
        return authUseCase.isAuthenticated()
    }
}
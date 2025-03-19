package ru.hse.cardefectscan.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.hse.cardefectscan.domain.usecase.AuthUseCase
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
) : CommonViewModel() {
    var isLoading by mutableStateOf(false)

    suspend fun logout() {
        Log.d("SettingsViewModel", "Logout launched")
        isLoading = true
        runCatchingWithHandling {
            authUseCase.logout()
        }
        Log.d("SettingsViewModel", "Logout processed, set isLoading to false")
        isLoading = false
    }
}
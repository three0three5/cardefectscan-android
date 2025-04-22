package ru.hse.cardefectscan.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.hse.cardefectscan.domain.usecase.AuthUseCase
import ru.hse.cardefectscan.domain.usecase.SettingsUseCase
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val settingsUseCase: SettingsUseCase,
) : CommonViewModel() {
    var isLoading by mutableStateOf(false)
    var cacheSize by mutableStateOf("0 KB")
        private set

    init {
        updateCacheSize()
    }

    suspend fun logout() {
        Log.d("SettingsViewModel", "Logout launched")
        isLoading = true
        runCatchingWithHandling {
            authUseCase.logout()
        }
        Log.d("SettingsViewModel", "Logout processed, set isLoading to false")
        isLoading = false
    }

    fun clearCache() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading = true
            settingsUseCase.clearCache()
            updateCacheSize()
            isLoading = false
        }
    }

    private fun updateCacheSize() {
        viewModelScope.launch(Dispatchers.IO) {
            cacheSize = settingsUseCase.getCacheSize()
        }
    }
}
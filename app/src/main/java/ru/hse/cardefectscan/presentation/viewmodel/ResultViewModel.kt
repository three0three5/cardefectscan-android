package ru.hse.cardefectscan.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.hse.cardefectscan.domain.usecase.ProcessedResult
import ru.hse.cardefectscan.domain.usecase.RequestsUseCase
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val requestsUseCase: RequestsUseCase,
) : CommonViewModel() {
    var isLoading by mutableStateOf(false)
    var result: ProcessedResult? by mutableStateOf(null)

    suspend fun loadData(imageId: String) {
        isLoading = true
        runCatchingWithHandling {
            result = requestsUseCase.getOriginalAndRenderedDrawable(imageId)
        }
        isLoading = false
    }

    fun generateColor(label: Int) = requestsUseCase.generateColor(label)
}
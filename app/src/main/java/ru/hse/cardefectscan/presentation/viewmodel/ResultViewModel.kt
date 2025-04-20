package ru.hse.cardefectscan.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.hse.cardefectscan.domain.usecase.ProcessedResult
import ru.hse.cardefectscan.domain.usecase.RequestsUseCase
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val requestsUseCase: RequestsUseCase,
) : CommonViewModel() {
    var isLoading by mutableStateOf(false)
    var result: ProcessedResult? by mutableStateOf(null)

    fun loadData(imageId: String) =
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                if (isLoading) return@withContext
                isLoading = true
                runCatchingWithHandling {
                    result = requestsUseCase.getOriginalAndRenderedDrawable(imageId)
                }
                isLoading = false
            }
        }

    fun generateColor(label: Int) = requestsUseCase.generateColor(label)
}
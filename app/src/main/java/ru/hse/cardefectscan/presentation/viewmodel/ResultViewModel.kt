package ru.hse.cardefectscan.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.hse.cardefectscan.domain.usecase.ProcessedResult
import ru.hse.cardefectscan.domain.usecase.RequestsUseCase
import ru.hse.generated.models.ImageRequestStatus
import javax.inject.Inject

private val FINAL_STATUSES = listOf(
    ImageRequestStatus.DONE,
    ImageRequestStatus.FAILED,
)

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val requestsUseCase: RequestsUseCase,
) : CommonViewModel() {
    var isLoading by mutableStateOf(false)
    var result: ProcessedResult? by mutableStateOf(null)

    fun loadData(imageId: String) {
        viewModelScope.launch {
            isLoading = true
            runCatchingWithHandling {
                // проверить кэш по imageId

                result = requestsUseCase.getOriginalAndRenderedDrawable(imageId)
                if (result?.status in FINAL_STATUSES) {
                    // кэшировать на диске
                }
            }
            isLoading = false
        }
    }

    fun generateColor(label: Int) = requestsUseCase.generateColor(label)
}
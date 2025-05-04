package ru.hse.cardefectscan.presentation.viewmodel

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.hse.cardefectscan.domain.usecase.ProcessedResult
import ru.hse.cardefectscan.domain.usecase.RequestsUseCase
import ru.hse.cardefectscan.domain.usecase.ResultUseCase
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val requestsUseCase: RequestsUseCase,
    private val resultUseCase: ResultUseCase,
) : CommonViewModel() {
    var isLoading by mutableStateOf(false)
    var isRendering by mutableStateOf(false)
    var result: ProcessedResult? by mutableStateOf(null)
    var renderedBitmap by mutableStateOf<Bitmap?>(null)
        private set
    var transparencyCoefficient: Float by mutableFloatStateOf(0.5f)

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

    fun renderImage(resultBitmap: Bitmap, originalBitmap: Bitmap) =
        viewModelScope.launch {
            renderedBitmap = withContext(Dispatchers.Default) {
                if (isRendering) return@withContext null
                isRendering = true
                val res = resultUseCase.getRenderedImage(
                    originalBitmap,
                    resultBitmap,
                    1 - transparencyCoefficient,
                )
                isRendering = false
                res
            }
        }


    fun generateColor(label: Int) = resultUseCase.generateColor(label)
}
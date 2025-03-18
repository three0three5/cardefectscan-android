package ru.hse.cardefectscan.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.hse.cardefectscan.presentation.exception.CommonException
import ru.hse.cardefectscan.utils.UNKNOWN_EXCEPTION
import ru.hse.generated.infrastructure.ClientException
import ru.hse.generated.infrastructure.ServerException

open class CommonViewModel : ViewModel() {
    private var messageJob: Job? = null
    var exceptionMessage by mutableStateOf("")
    var displayMessage by mutableStateOf(false)

    suspend fun <T> runCatchingWithHandling(block: suspend () -> T): T? {
        val result = runCatching {
            block.invoke()
        }
        if (result.isFailure) {
            when (val throwable = result.exceptionOrNull()!!) {
                is CommonException -> {
                    Log.w("CommonViewModel", "Handled exception: $throwable with cause ${throwable.cause}")
                    exceptionMessage = result.exceptionOrNull()!!.message ?: UNKNOWN_EXCEPTION
                }
                is ClientException -> {
                    Log.d("CommonViewModel", "Client exception: ${throwable.message}")
                }
                is ServerException -> {
                    Log.d("CommonViewModel", "Server exception: ${throwable.message}")
                }
                else -> throw throwable
            }
            messageJob?.cancel()
            messageJob = viewModelScope.launch {
                Log.d("CommonViewModel", "Display error message")
                displayMessage = true
                delay(10000)
                Log.d("CommonViewModel", "Hide error message")
                displayMessage = false
            }
        } else {
            exceptionMessage = ""
        }
        return result.getOrNull()
    }
}
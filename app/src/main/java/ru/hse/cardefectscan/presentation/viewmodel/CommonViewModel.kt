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
import org.json.JSONObject
import ru.hse.cardefectscan.presentation.exception.CommonException
import ru.hse.cardefectscan.utils.UNKNOWN_EXCEPTION
import ru.hse.cardefectscan.utils.UtilsExtensions.notBlank
import ru.hse.generated.infrastructure.ClientError
import ru.hse.generated.infrastructure.ClientException
import ru.hse.generated.infrastructure.ServerError
import ru.hse.generated.infrastructure.ServerException

abstract class CommonViewModel : ViewModel() {
    private var messageJob: Job? = null
    var exceptionMessage by mutableStateOf("")
    var displayMessage by mutableStateOf(false)

    suspend fun <T> runCatchingWithHandling(block: suspend () -> T): T? {
        messageJob?.cancel()
        exceptionMessage = ""
        displayMessage = false
        val result = runCatching {
            block.invoke()
        }
        return handleResult(result)
    }

    fun <T> handleResult(result: Result<T>): T? {
        if (result.isFailure) {
            when (val throwable = result.exceptionOrNull()!!) {
                is CommonException -> {
                    Log.w(
                        "CommonViewModel",
                        "Handled exception: $throwable with cause ${throwable.cause}"
                    )
                    exceptionMessage = throwable.message?.notBlank() ?: UNKNOWN_EXCEPTION
                }

                is ClientException -> {
                    Log.d("CommonViewModel", "Client exception: ${throwable.message}")

                    val clientError = throwable.response as? ClientError<*>
                    val responseBody = clientError?.body as? String
                    Log.d("CommonViewModel", "Response body from client error: $responseBody")

                    if (throwable.statusCode == 413) {
                        exceptionMessage = "Файл слишком большой. Загрузите изображение до 25 мб"
                    } else {
                        exceptionMessage = extractDetails(responseBody)
                    }
                }

                is ServerException -> {
                    Log.d("CommonViewModel", "Server exception: ${throwable.message}")
                    val error = throwable.response as? ServerError<*>
                    val responseBody = error?.body as? String
                    Log.d("CommonViewModel", "Response body from server error: $responseBody")

                    exceptionMessage = extractDetails(responseBody)
                }

                else -> throw throwable
            }
            messageJob = viewModelScope.launch {
                Log.d("CommonViewModel", "Display error message: $exceptionMessage")
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

    private fun extractDetails(responseBody: String?) = try {
        val json = JSONObject(responseBody ?: "{}")
        json.optString(DETAIL_FIELD, UNKNOWN_EXCEPTION)
    } catch (e: Exception) {
        UNKNOWN_EXCEPTION
    }

    companion object {
        const val DETAIL_FIELD = "detail"
    }
}
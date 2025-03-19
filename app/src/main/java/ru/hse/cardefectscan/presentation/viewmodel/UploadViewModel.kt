package ru.hse.cardefectscan.presentation.viewmodel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import ru.hse.cardefectscan.domain.usecase.ImageUseCase
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val imageUseCase: ImageUseCase,
) : CommonViewModel() {
    var imageUri by mutableStateOf<Uri?>(null)
    var isLoading by mutableStateOf(false)
    var loaded by mutableStateOf(false)

    suspend fun upload() {
        isLoading = true
        Log.d("UploadScreen", "Trying to upload the image")
        delay(10000)
        Log.d("UploadScreen", "Finished uploading")
        isLoading = false
        loaded = true
    }
}
package ru.hse.cardefectscan.presentation.viewmodel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.hse.cardefectscan.domain.usecase.ImageUseCase
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val imageUseCase: ImageUseCase,
) : CommonViewModel() {
    var imageUri by mutableStateOf<Uri?>(null)
    var isLoading by mutableStateOf(false)
    var loaded by mutableStateOf(false)
    val photoUri = mutableStateOf<Uri?>(null)

    suspend fun upload() {
        isLoading = true
        Log.d("UploadViewModel", "Trying to upload the image")
        runCatchingWithHandling {
            imageUseCase.upload(imageUri!!)
        }
        Log.d("UploadViewModel", "Finished uploading")
        isLoading = false
        loaded = true
    }
}
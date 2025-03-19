package ru.hse.cardefectscan.presentation.viewmodel

import android.net.Uri
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


}
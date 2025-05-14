package ru.hse.cardefectscan.presentation.viewmodel

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.provider.MediaStore
import android.os.Environment
import android.util.Log
import android.widget.Toast
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
import ru.hse.cardefectscan.utils.UtilsExtensions.formatDate
import java.io.OutputStream
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
    var labelSet by mutableStateOf<Set<Pair<Int, Int>>?>(null)
        private set
    var selectedSegment by mutableStateOf<Pair<Int, Int>?>(null)

    fun onPixelClicked(x: Int, y: Int) {
        Log.d("ResultViewModel", "onPixelClicked: $x, $y")
        val mask = result?.result ?: return
        val readableMask = if (mask.config == Bitmap.Config.HARDWARE) {
            mask.copy(Bitmap.Config.ARGB_8888, false)
        } else {
            mask
        }
        val code = readableMask.getPixel(x, y)
        val segment = Color.red(code)
        val damage = Color.green(code)
        selectedSegment = segment to damage
    }

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

    fun renderImage() =
        viewModelScope.launch {
            val resultBitmap = result?.result
            val originalBitmap = result?.original
            if (resultBitmap == null || originalBitmap == null) return@launch
            renderedBitmap = withContext(Dispatchers.Default) {
                if (isRendering) return@withContext null
                isRendering = true
                val res = resultUseCase.getRenderedImageWithLabels(
                    originalBitmap,
                    resultBitmap,
                    1 - transparencyCoefficient,
                )
                isRendering = false
                labelSet = res.second
                res.first
            }
        }

    fun saveBitmapToGallery(context: Context) {
        val date = result?.createdAt?.formatDate() ?: return

        val filename = "segmentation_result_${date}.png"
        val resolver = context.contentResolver
        val imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/CarDefectScan")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val bitmap = renderedBitmap ?: return
        val imageUri = resolver.insert(imageCollection, contentValues)
        imageUri?.let { uri ->
            resolver.openOutputStream(uri)?.use { stream: OutputStream ->
                if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)) {
                    contentValues.clear()
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                    resolver.update(uri, contentValues, null, null)
                    Toast.makeText(context, "Изображение сохранено", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Не удалось сохранить изображение", Toast.LENGTH_SHORT).show()
                }
            }
        } ?: run {
            Toast.makeText(context, "Ошибка доступа к хранилищу", Toast.LENGTH_SHORT).show()
        }
    }

    fun generateColor(segment: Int, damage: Int) = resultUseCase.generateColor(segment, damage)
}
package ru.hse.cardefectscan.presentation.ui.components

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import ru.hse.cardefectscan.domain.usecase.ProcessedResult
import ru.hse.cardefectscan.presentation.viewmodel.ResultViewModel
import ru.hse.cardefectscan.utils.DAMAGE_LEVEL_TRANSCRIPTIONS
import ru.hse.cardefectscan.utils.LABEL_TRANSCRIPTIONS
import ru.hse.cardefectscan.utils.STATUS_TRANSCRIPTION
import ru.hse.cardefectscan.utils.UtilsExtensions.formatDate

@Composable
fun ResultScreen(
    imageId: String,
    vm: ResultViewModel = hiltViewModel(),
) {
    vm.loadData(imageId)
    Scaffold { innerPadding ->
        if (vm.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(innerPadding)
            )
        } else {
            vm.result?.let {
                ProcessedResultComponent(
                    vm,
                    it,
                    padding = innerPadding,
                )
            }
        }
        vm.exceptionMessage.takeUnless { it.isBlank() }?.let {
            Text(
                vm.exceptionMessage,
                color = Color.Red,
                modifier = Modifier
                    .padding(innerPadding)
            )
        }
    }
}

@Composable
fun ProcessedResultComponent(
    vm: ResultViewModel,
    result: ProcessedResult,
    padding: PaddingValues,
) {
    val originalBitmap = result.original
    val status = STATUS_TRANSCRIPTION[result.status] ?: "Статус неизвестен: ${result.status.value}"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(padding)
    ) {
        Text(text = "Статус запроса: $status")
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Оригинальное изображение", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        OriginalImage(originalBitmap)
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Загружено: ${result.createdAt.formatDate()}", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Результат сегментации", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        ResultImage(vm,
            onPixelClick = { x, y -> vm.onPixelClicked(x, y) }
        )
        Spacer(modifier = Modifier.height(16.dp))

        vm.selectedSegment?.let {
            Text(text = "Выбранный сегмент: (${it.first}, ${it.second})", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
            LegendRow(vm, it.first, it.second)
            Spacer(modifier = Modifier.height(16.dp))
        }

        vm.result?.result?.let {
            if (vm.isRendering) {
                CircularProgressIndicator()
            } else {
                Text(text = "Выбрать уровень прозрачности маски сегментации", style = MaterialTheme.typography.bodyMedium)
                TransparencySlider(vm)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Text(text = "Статус обновлен: ${result.updatedAt.formatDate()}", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Описание сегментов", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Legend(vm)
        Spacer(modifier = Modifier.height(16.dp))

        vm.renderedBitmap?.let {
            DownloadResultButton(vm)
        }

        result.description?.let {
            Text(text = "Описание ошибки: $it", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun DownloadResultButton(
    vm: ResultViewModel,
) {
    val context = LocalContext.current
    Button(onClick = {
        vm.saveBitmapToGallery(context)
    }) {
        Text("Скачать результат")
    }
}

@Composable
private fun TransparencySlider(
    vm: ResultViewModel,
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Slider(
            value = vm.transparencyCoefficient,
            onValueChange = { vm.transparencyCoefficient = it },
            valueRange = 0f..1f,
            steps = 20,
        )
        Text(
            text = "Текущая прозрачность: ${(vm.transparencyCoefficient * 100).toInt()}%",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

@Composable
private fun Legend(
    vm: ResultViewModel,
) {
    vm.labelSet?.let {
            it.filter { p ->
                p.first != 0
            }
            .forEach { (segment, damage) ->
                LegendRow(vm, segment, damage)
            }
    } ?: run {
        Text(
            text = "Информация о сегментах отсутствует",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

@OptIn(FlowPreview::class)
@Composable
private fun ResultImage(
    vm: ResultViewModel,
    onPixelClick: (Int, Int) -> Unit
) {
    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    LaunchedEffect(vm.transparencyCoefficient) {
        snapshotFlow { vm.transparencyCoefficient }
            .debounce(500)
            .collectLatest { vm.renderImage() }
    }

    vm.renderedBitmap?.let { bmp ->
        Image(
            bitmap = bmp.asImageBitmap(),
            contentDescription = "Результат",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, Color.LightGray)
                .onGloballyPositioned { coords ->
                    containerSize = coords.size
                }
                .pointerInput(bmp, containerSize) {
                    detectTapGestures { tap ->
                        // Получаем коэффициент во сколько раз увеличена картинка чтобы получить ее размер в контейнере
                        val scaleX = containerSize.width.toFloat()  / bmp.width
                        val scaleY = containerSize.height.toFloat() / bmp.height
                        val scale  = minOf(scaleX, scaleY)

                        // Получаем начало координат картинки внутри контейнера
                        val offsetX = (containerSize.width - bmp.width * scale) / 2f
                        val offsetY = (containerSize.height - bmp.height * scale) / 2f

                        // Локальная координата тапа относительно начала bmp
                        val localX = (tap.x - offsetX).coerceIn(0f, bmp.width * scale)
                        val localY = (tap.y - offsetY).coerceIn(0f, bmp.height * scale)

                        // В пиксели оригинала
                        val bmpX = (localX / scale).toInt().coerceIn(0, bmp.width  - 1)
                        val bmpY = (localY / scale).toInt().coerceIn(0, bmp.height - 1)

                        Log.d("ResultScreen", "Tapped bitmap pixel: $bmpX, $bmpY")
                        onPixelClick(bmpX, bmpY)
                    }
                }
        )
    } ?: run {
        if (vm.isRendering) {
            CircularProgressIndicator()
        } else {
            Text(
                text = "Результат недоступен",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun OriginalImage(
    originalBitmap: Bitmap?,
) {
    if (originalBitmap != null) {
        Image(
            bitmap = originalBitmap.asImageBitmap(),
            contentDescription = "Оригинал",
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, Color.LightGray)
        )
    } else {
        Text(
            text = "Оригинальное изображение недоступно",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

@Composable
private fun LegendRow(
    vm: ResultViewModel,
    segment: Int,
    damage: Int,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        val color = Color(vm.generateColor(segment, damage))
        val damageLevel = DAMAGE_LEVEL_TRANSCRIPTIONS.getOrNull(damage)
            ?: run {
                Log.e("ResultScreen", "damage label invalid $damage")
                "Неизвестный тип повреждения"
            }
        val segmentName = LABEL_TRANSCRIPTIONS.getOrNull(segment)
            ?: run {
                Log.e("ResultScreen", "segment label invalid $segment")
                "Неизвестный тип сегмента"
            }
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(color)
                .border(1.dp, Color.Black)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$damageLevel: $segmentName",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    device = Devices.PIXEL,
)
@Composable
fun ResultScreenPreview() {
    ResultScreen("1234")
}
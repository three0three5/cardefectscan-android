package ru.hse.cardefectscan.presentation.ui.components

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ru.hse.cardefectscan.domain.usecase.ProcessedResult
import ru.hse.cardefectscan.presentation.viewmodel.ResultViewModel
import ru.hse.cardefectscan.utils.DAMAGE_LEVEL_TRANSCRIPTIONS
import ru.hse.cardefectscan.utils.LABEL_TRANSCRIPTIONS
import ru.hse.cardefectscan.utils.STATUS_TRANSCRIPTION
import ru.hse.generated.models.ResultMetadata

@Composable
fun ResultScreen(
    imageId: String,
    vm: ResultViewModel = hiltViewModel(),
) {
    Log.d("ResultScreen", "Loading data")
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
    }
}

@Composable
fun ProcessedResultComponent(
    vm: ResultViewModel,
    result: ProcessedResult,
    padding: PaddingValues,
) {
    val originalBitmap = result.original
    val renderedBitmap = result.result?.first
    val legendData = result.result?.second?.result
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

        Text(text = "Загружено: ${result.createdAt}", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Результат сегментации", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        ResultImage(renderedBitmap)
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Статус обновлен: ${result.updatedAt}", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Описание сегментов", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Legend(legendData, vm)
        Spacer(modifier = Modifier.height(16.dp))

        result.description?.let {
            Text(text = "Описание ошибки: $it", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
        }

        DisplayMessage(vm)
    }
}

@Composable
private fun Legend(
    legendData: Map<String, ResultMetadata>?,
    vm: ResultViewModel,
) {
    if (!legendData.isNullOrEmpty()) {
        legendData.entries
            .mapNotNull { entry ->
                entry.key.toIntOrNull()?.let { key -> key to entry.value }
            }
            .filter { it.first != 0 }
            .sortedBy { it.first }
            .forEach { (label, metadata) ->
                LegendRow(vm, label, metadata)
            }
    } else {
        Text(
            text = "Информация о сегментах отсутствует",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

@Composable
private fun ResultImage(
    renderedBitmap: Bitmap?,
) {
    if (renderedBitmap != null) {
        Image(
            bitmap = renderedBitmap.asImageBitmap(),
            contentDescription = "Результат",
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, Color.LightGray)
        )
    } else {
        Text(
            text = "Результат недоступен",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
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
    label: Int,
    metadata: ResultMetadata,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        val color = Color(vm.generateColor(label))
        val damageLevel = DAMAGE_LEVEL_TRANSCRIPTIONS[metadata.damageLevel]
            ?: "Неопределенный тип повреждения: ${metadata.damageLevel}"
        val segmentName = LABEL_TRANSCRIPTIONS[metadata.segmentName]
            ?: "Неизвестный сегмент: ${metadata.segmentName}"
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
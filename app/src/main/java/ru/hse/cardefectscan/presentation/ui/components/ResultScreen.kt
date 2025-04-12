package ru.hse.cardefectscan.presentation.ui.components

import android.graphics.Color
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import ru.hse.cardefectscan.domain.usecase.ProcessedResult
import ru.hse.cardefectscan.presentation.viewmodel.ResultViewModel

@Composable
fun ResultScreen(
    imageId: String,
    vm: ResultViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        Log.d("ResultScreen", "Loading data")
        vm.loadData(imageId)
    }
    Scaffold { innerPadding ->
        Text(
            imageId,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            style = MaterialTheme.typography.titleLarge
        )
        if (vm.isLoading) {
            CircularProgressIndicator()
        }
        vm.result?.let {
            ProcessedResultComponent(it)
        }
    }
    DisplayMessage(vm)
}

@Composable
fun ProcessedResultComponent(result: ProcessedResult) {
    val set: MutableSet<Int> = mutableSetOf()
    val img = result.result ?: return

    val bitmap = img.first.asAndroidBitmap()
    Log.d("ResultScreen", "Metadata: ${img.second}")

    val width = bitmap.width
    val height = bitmap.height

    for (y in 0 until height) {
        for (x in 0 until width) {
            val pixel = bitmap.getPixel(x, y)
            val value = Color.red(pixel)
            set.add(value)
        }
    }
    Log.d("ResultScreen", "Unique values: $set")
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
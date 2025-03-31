package ru.hse.cardefectscan.presentation.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import ru.hse.cardefectscan.presentation.viewmodel.ResultViewModel

@Composable
fun ResultScreen(
    imageId: String,
    vm: ResultViewModel = hiltViewModel(),
) {
    Scaffold { innerPadding ->
        Text(
            imageId,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            style = MaterialTheme.typography.titleLarge
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
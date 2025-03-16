package ru.hse.cardefectscan.presentation.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun LoginScreen() {
    Scaffold { innerPadding ->
        Text(
            "TODO",
            modifier = Modifier
                .padding(innerPadding),
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
fun LoginScreenPreview() {
    LoginScreen()
}
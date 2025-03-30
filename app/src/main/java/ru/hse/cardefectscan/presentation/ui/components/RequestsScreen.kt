package ru.hse.cardefectscan.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun RequestsScreen(
    //vm: RequestsViewModel,
) {
    //val items = vm.pagingFlow.collectAsLazyPagingItems()


    Scaffold { innerPadding ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(10) { index ->
                RequestElement()
            }
        }
    }
}

@Composable
fun RequestElement() {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val buttonHeight = screenHeight * 0.12f
    Button(
        onClick = {
            // TODO
        },
        modifier = Modifier
            .height(buttonHeight)
            .fillMaxWidth(0.9f)
    ) {

    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    device = Devices.PIXEL,
)
@Composable
fun RequestsScreenPreview() {
    RequestsScreen()
}
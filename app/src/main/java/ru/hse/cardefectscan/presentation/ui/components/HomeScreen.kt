package ru.hse.cardefectscan.presentation.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ru.hse.cardefectscan.presentation.viewmodel.HomeViewModel
import ru.hse.cardefectscan.utils.LOGIN_SCREEN
import ru.hse.cardefectscan.utils.REQUESTS_SCREEN
import ru.hse.cardefectscan.utils.SETTINGS_SCREEN
import ru.hse.cardefectscan.utils.TO_REQUESTS_SCREEN
import ru.hse.cardefectscan.utils.TO_SETTINGS_SCREEN
import ru.hse.cardefectscan.utils.TO_UPLOAD_SCREEN
import ru.hse.cardefectscan.utils.UPLOAD_SCREEN

@Composable
fun HomeScreen(
    navController: NavController,
    vm: HomeViewModel = hiltViewModel(),
) {
    if (!vm.isAuthenticated()) {
        navController.navigate(LOGIN_SCREEN)
    } else {
        HomeScaffold(navController)
    }
}

@Composable
fun HomeScaffold(
    navController: NavController
) {
    Scaffold { innerPadding ->
        val topPadding = LocalConfiguration.current.screenHeightDp.dp * 0.1f
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .padding(top = topPadding)
                .fillMaxWidth()
                .fillMaxHeight(0.8f),
            contentAlignment = Alignment.Center
        ) {
            HomeButtonSet(navController)
        }
    }
}

@Composable
fun HomeButtonSet(navController: NavController) {
    val startPadding = LocalConfiguration.current.screenWidthDp.dp * 0.1f
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = startPadding),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ToRequestsButton(navController)
        ToUploadButton(navController)
        ToSettingsButton(navController)
    }
}

@Composable
fun ToRequestsButton(navController: NavController) {
    Button(
        modifier = Modifier
            .fillMaxHeight(0.25f)
            .fillMaxWidth(0.8f),
        onClick = { navController.navigate(REQUESTS_SCREEN) }
    ) {
        Text(
            TO_REQUESTS_SCREEN,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
fun ToUploadButton(navController: NavController) {
    Button(
        modifier = Modifier
            .fillMaxHeight(0.25f)
            .fillMaxWidth(0.8f),
        onClick = { navController.navigate(UPLOAD_SCREEN) }
    ) {
        Text(
            TO_UPLOAD_SCREEN,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
fun ToSettingsButton(navController: NavController) {
    Button(
        modifier = Modifier
            .fillMaxHeight(0.35f)
            .fillMaxWidth(0.8f),
        onClick = { navController.navigate(SETTINGS_SCREEN) }
    ) {
        Text(
            TO_SETTINGS_SCREEN,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    device = Devices.PIXEL,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
    wallpaper = Wallpapers.RED_DOMINATED_EXAMPLE,
)
@Composable
fun HomeScreenPreview() {
    HomeScreen(rememberNavController())
}
package ru.hse.cardefectscan.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.hse.cardefectscan.presentation.viewmodel.SettingsViewModel
import ru.hse.cardefectscan.utils.LOGIN_SCREEN
import ru.hse.cardefectscan.utils.LOGOUT_BUTTON

@Composable
fun SettingsScreen(
    navController: NavController,
    vm: SettingsViewModel = hiltViewModel(),
    scope: CoroutineScope = rememberCoroutineScope(),
) {
    Scaffold { innerPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize(0.6f)
            ) {
                LogoutButton(
                    navController,
                    vm,
                    scope,
                )
                if (vm.isLoading) {
                    CircularProgressIndicator()
                }
                WithAnimation(vm.displayMessage) {
                    Text(vm.exceptionMessage, color = Color.Red)
                }
            }
        }
    }
}

@Composable
fun LogoutButton(
    navController: NavController,
    vm: SettingsViewModel,
    scope: CoroutineScope,
) {
    Button(
        onClick = {
            if (vm.isLoading) return@Button
            scope.launch {
                vm.logout()
                if (vm.exceptionMessage.isBlank()) {
                    navController.navigate(LOGIN_SCREEN)
                }
            }
        }
    ) {
        Text(
            LOGOUT_BUTTON,
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
fun SettingsScreenPreview() {
    SettingsScreen(rememberNavController())
}
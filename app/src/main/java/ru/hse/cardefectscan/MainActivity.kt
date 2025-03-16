package ru.hse.cardefectscan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.hse.cardefectscan.presentation.ui.theme.CarDefectScanTheme
import ru.hse.cardefectscan.utils.HOME_SCREEN
import ru.hse.cardefectscan.utils.LOGIN_SCREEN
import ru.hse.cardefectscan.utils.REQUESTS_SCREEN
import ru.hse.cardefectscan.utils.RESULT_SCREEN
import ru.hse.cardefectscan.utils.SETTINGS_SCREEN
import ru.hse.cardefectscan.utils.UPLOAD_SCREEN

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CarDefectScanTheme {
                val navController = rememberNavController()
                val startDestination = HOME_SCREEN
                NavHost(navController = navController, startDestination = startDestination) {
                    composable(HOME_SCREEN) { HomeScreen() }
                    composable(LOGIN_SCREEN) { LoginScreen() }
                    composable(REQUESTS_SCREEN) { RequestsScreen() }
                    composable(RESULT_SCREEN) { ResultScreen() }
                    composable(SETTINGS_SCREEN) { SettingsScreen() }
                    composable(UPLOAD_SCREEN) { UploadScreen() }
                }
            }
        }
    }
}

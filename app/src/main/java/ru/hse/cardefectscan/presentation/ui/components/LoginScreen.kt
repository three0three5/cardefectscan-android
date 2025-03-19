package ru.hse.cardefectscan.presentation.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ru.hse.cardefectscan.presentation.viewmodel.LoginViewModel
import ru.hse.cardefectscan.utils.ENTER_ADDITIONAL_PASSWORD_LABEL
import ru.hse.cardefectscan.utils.ENTER_LOGIN_LABEL
import ru.hse.cardefectscan.utils.ENTER_LOGIN_PLACEHOLDER
import ru.hse.cardefectscan.utils.ENTER_PASSWORD_LABEL
import ru.hse.cardefectscan.utils.ENTER_PASSWORD_PLACEHOLDER
import ru.hse.cardefectscan.utils.HOME_SCREEN
import ru.hse.cardefectscan.utils.LOGIN_BUTTON
import ru.hse.cardefectscan.utils.LOGIN_LABEL
import ru.hse.cardefectscan.utils.LOGIN_MODE_BUTTON
import ru.hse.cardefectscan.utils.SIGNUP_BUTTON
import ru.hse.cardefectscan.utils.SIGNUP_LABEL
import ru.hse.cardefectscan.utils.SIGNUP_MODE_BUTTON

@Composable
fun LoginScreen(navController: NavController) {
    Scaffold { innerPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LoginElements(navController)
        }
    }
}

@Composable
fun LoginElements(
    navController: NavController,
    vm: LoginViewModel = hiltViewModel(),
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        val label = if (vm.isLogin) LOGIN_LABEL else SIGNUP_LABEL

        TitleLabel(label)
        LoginField(vm)
        PasswordField(vm)
        WithAnimation(!vm.isLogin) {
            AdditionalPasswordField(vm)
        }
        if (vm.isLogin) {
            LoginButton(vm, navController)
            SignupModeButton(vm)
        } else {
            SignupButton(vm, navController)
            LoginModeButton(vm)
        }
        if (vm.isLoading) {
            CircularProgressIndicator()
        }
        DisplayMessage(vm)
    }
}

@Composable
fun LoginModeButton(vm: LoginViewModel) {
    Button(
        onClick = {
            if (vm.isLoading) return@Button
            vm.toggleLoginMode()
        },
        modifier = Modifier
            .fillMaxHeight(0.18f)
            .fillMaxWidth(0.6f),
    ) {
        Text(LOGIN_MODE_BUTTON)
    }
}

@Composable
fun SignupButton(vm: LoginViewModel, navController: NavController) {
    LaunchedEffect(Unit) {
        snapshotFlow { vm.isLoading }
            .collect { isLoading ->
                if (isLoading) {
                    Log.d("LoginScreen", "Launch signup effect")
                    vm.signup()
                    vm.isLoading = false
                    if (vm.exceptionMessage == "") {
                        Log.d("LoginScreen", "Exception message is empty; navigate to home")
                        navController.navigate(HOME_SCREEN)
                    } else {
                        Log.d("LoginScreen", "Exception message is not empty; skip navigate")
                    }
                }
            }
    }

    Button(
        onClick = {
            if (vm.isLoading) return@Button
            vm.isLoading = true
        },
        modifier = Modifier
            .fillMaxHeight(0.18f)
            .fillMaxWidth(0.45f),
    ) {
        Text(SIGNUP_BUTTON)
    }
}

@Composable
fun AdditionalPasswordField(vm: LoginViewModel) {
    OutlinedTextField(
        value = vm.additionalPassword,
        onValueChange = { vm.additionalPassword = it },
        label = { Text(ENTER_ADDITIONAL_PASSWORD_LABEL) },
        visualTransformation = PasswordVisualTransformation(),
        placeholder = {
            Text(
                ENTER_PASSWORD_PLACEHOLDER,
                color = Color.Gray.copy(alpha = 0.5f)
            )
        },
    )
}

@Composable
fun SignupModeButton(vm: LoginViewModel) {
    Button(
        onClick = {
            if (vm.isLoading) return@Button
            vm.toggleLoginMode()
        },
        modifier = Modifier
            .fillMaxHeight(0.14f)
            .fillMaxWidth(0.4f),
    ) {
        Text(SIGNUP_MODE_BUTTON)
    }
}

@Composable
fun LoginButton(
    vm: LoginViewModel,
    navController: NavController,
) {
    LaunchedEffect(Unit) {
        snapshotFlow { vm.isLoading }
            .collect { isLoading ->
                if (vm.isLoading) {
                    Log.d("LoginScreen", "Launch login effect")
                    vm.login()
                    vm.isLoading = false
                    if (vm.exceptionMessage == "") {
                        Log.d("LoginScreen", "Exception message is empty; navigate to home")
                        navController.navigate(HOME_SCREEN)
                    } else {
                        Log.d("LoginScreen", "Exception message is not empty; skip navigate")
                    }
                }
            }
    }

    Button(
        onClick = {
            if (vm.isLoading) return@Button
            vm.isLoading = true
        },
        modifier = Modifier
            .fillMaxHeight(0.1f)
            .fillMaxWidth(0.4f),
    ) {
        Log.d("LoginScreen", "Entered login ${vm.login} and password ${vm.password}")
        Text(LOGIN_BUTTON)
    }
}

@Composable
fun PasswordField(vm: LoginViewModel) {
    OutlinedTextField(
        value = vm.password,
        onValueChange = { vm.password = it },
        label = { Text(ENTER_PASSWORD_LABEL) },
        visualTransformation = PasswordVisualTransformation(),
        placeholder = {
            Text(
                ENTER_PASSWORD_PLACEHOLDER,
                color = Color.Gray.copy(alpha = 0.5f)
            )
        },
    )
}

@Composable
fun LoginField(vm: LoginViewModel) {
    OutlinedTextField(
        vm.login,
        onValueChange = {
            vm.login = it
        },
        label = { Text(ENTER_LOGIN_LABEL) },
        placeholder = {
            Text(
                ENTER_LOGIN_PLACEHOLDER,
                color = Color.Gray.copy(alpha = 0.5f),
            )
        }
    )
}

@Composable
fun TitleLabel(label: String) {
    Text(
        label,
        style = MaterialTheme.typography.titleLarge
    )
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    device = Devices.PIXEL,
)
@Composable
fun LoginScreenPreview() {
    LoginScreen(rememberNavController())
}
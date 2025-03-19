package ru.hse.cardefectscan.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.hse.cardefectscan.domain.usecase.AuthUseCase
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
) : CommonViewModel() {
    var isLogin by mutableStateOf(true)
    var login by mutableStateOf("")
    var password by mutableStateOf("")
    var additionalPassword by mutableStateOf("")
    var isLoading by mutableStateOf(false)

    suspend fun signup() {
        Log.d("LoginScreen", "Launch signup effect")
        isLoading = true
        runCatchingWithHandling {
            authUseCase.signup(login, password, additionalPassword)
        }
        isLoading = false
        Log.d("LoginViewModel", "Processed signup")
    }

    suspend fun login() {
        isLoading = true
        runCatchingWithHandling {
            authUseCase.login(login, password)
        }
        isLoading = false
        Log.d("LoginViewModel", "login processed")
    }

    fun toggleLoginMode() {
        isLogin = !isLogin
    }
}
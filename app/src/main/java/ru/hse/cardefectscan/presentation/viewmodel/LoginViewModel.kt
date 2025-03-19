package ru.hse.cardefectscan.presentation.viewmodel

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
        runCatchingWithHandling {
            authUseCase.signup(login, password, additionalPassword)
        }
    }

    suspend fun login() {
        runCatchingWithHandling {
            authUseCase.login(login, password)
        }
    }

    fun toggleLoginMode() {
        isLogin = !isLogin
    }
}
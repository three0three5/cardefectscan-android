package ru.hse.cardefectscan.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    var isLogin by mutableStateOf(true)
    var login by mutableStateOf("")
    var password by mutableStateOf("")
    var additionalPassword by mutableStateOf("")

    fun signup() {
        // TODO
    }

    fun login() {
        // TODO
    }

    fun toggleLoginMode() {
        isLogin = !isLogin
    }
}
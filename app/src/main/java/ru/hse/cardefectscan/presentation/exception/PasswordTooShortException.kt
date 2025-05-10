package ru.hse.cardefectscan.presentation.exception

class PasswordTooShortException : InputException(
    "Пароль должен состоять из не менее чем 6 символов"
) {
}
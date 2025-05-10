package ru.hse.cardefectscan.presentation.exception

class LoginTooShortException : InputException(
    "Логин должен состоять не менее чем из 6 символов"
) {
}
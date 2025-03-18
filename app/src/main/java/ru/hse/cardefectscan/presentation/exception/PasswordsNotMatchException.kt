package ru.hse.cardefectscan.presentation.exception

import ru.hse.cardefectscan.utils.PASSWORDS_NOT_MATCH

class PasswordsNotMatchException(
    message: String? = PASSWORDS_NOT_MATCH
) : InputException(message) {
}
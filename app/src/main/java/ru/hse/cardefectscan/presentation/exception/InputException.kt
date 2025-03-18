package ru.hse.cardefectscan.presentation.exception

import ru.hse.cardefectscan.utils.UNKNOWN_EXCEPTION

class InputException(
    message: String? = UNKNOWN_EXCEPTION
) : RuntimeException(message) {
}
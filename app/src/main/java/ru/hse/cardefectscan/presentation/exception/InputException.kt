package ru.hse.cardefectscan.presentation.exception

import ru.hse.cardefectscan.utils.UNKNOWN_EXCEPTION
import java.io.IOException

open class InputException(
    message: String? = UNKNOWN_EXCEPTION
) : IOException(message) {
}
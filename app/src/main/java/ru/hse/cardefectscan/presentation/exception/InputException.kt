package ru.hse.cardefectscan.presentation.exception

import ru.hse.cardefectscan.utils.UNKNOWN_EXCEPTION

open class InputException(
    message: String? = UNKNOWN_EXCEPTION
) : CommonException(message) {
}
package ru.hse.cardefectscan.presentation.exception

import ru.hse.cardefectscan.utils.UNKNOWN_EXCEPTION

open class CommonException(
    message: String? = UNKNOWN_EXCEPTION
) : RuntimeException(message) {
}
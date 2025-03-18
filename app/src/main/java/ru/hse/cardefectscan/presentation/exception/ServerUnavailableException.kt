package ru.hse.cardefectscan.presentation.exception

import ru.hse.cardefectscan.utils.SERVER_ERROR

open class ServerUnavailableException(
    message: String? = SERVER_ERROR
) : CommonException(message) {
}
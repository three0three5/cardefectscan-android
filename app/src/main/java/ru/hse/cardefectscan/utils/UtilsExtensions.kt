package ru.hse.cardefectscan.utils

import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object UtilsExtensions {
    fun String.notBlank(): String? {
        if (this == "") return null
        return this
    }

    fun OffsetDateTime.formatDate(): String =
        this
            .atZoneSameInstant(ZoneId.systemDefault())
            .toLocalDateTime().format(
                DateTimeFormatter.ofPattern(
                    DATE_FORMAT
                )
            )
}
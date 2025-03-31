package ru.hse.cardefectscan.utils

import ru.hse.generated.models.ImageRequestStatus

val TRANSLATED_STATUS = mapOf(
    ImageRequestStatus.FAILED to "Завершен с ошибкой",
    ImageRequestStatus.DONE to "Завершен",
    ImageRequestStatus.IN_PROGRESS to "В обработке",
)

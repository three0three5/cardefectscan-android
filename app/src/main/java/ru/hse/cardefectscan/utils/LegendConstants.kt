package ru.hse.cardefectscan.utils

import ru.hse.generated.models.ImageRequestStatus
import ru.hse.generated.models.ResultMetadata.DamageLevel


val LABEL_TRANSCRIPTIONS = mapOf(
    "empty" to "пусто",
    "front_bumper" to "передний бампер",
    "rear_bumper" to "задний бампер",
    "left_door" to "левая дверь",
    "right_door" to "правая дверь",
    "hood" to "капот",
    "trunk" to "багажник",
    "roof" to "крыша",
    "left_fender" to "левое крыло",
    "right_fender" to "правое крыло",
    "left_headlight" to "левая фара",
    "right_headlight" to "правая фара",
    "left_taillight" to "левый задний фонарь",
    "right_taillight" to "правый задний фонарь",
    "grill" to "решетка радиатора",
    "windshield" to "лобовое стекло",
    "rear_window" to "заднее стекло",
    "side_mirror_left" to "левое боковое зеркало",
    "side_mirror_right" to "правое боковое зеркало"
)

val DAMAGE_LEVEL_TRANSCRIPTIONS = mapOf(
    DamageLevel.NONE to "нет повреждений",
    DamageLevel.SCRATCH to "царапина",
    DamageLevel.DENT to "вмятина",
    DamageLevel.CRACK to "трещина",
    DamageLevel.BROKEN to "разбит",
    DamageLevel.TOTAL_LOSS to "не подлежит восстановлению"
)

val STATUS_TRANSCRIPTION = mapOf(
    ImageRequestStatus.DONE to "Запрос успешно завершен",
    ImageRequestStatus.IN_PROGRESS to "Запрос в процессе обработки",
    ImageRequestStatus.FAILED to "Запрос завершен с ошибкой",
)

package ru.hse.cardefectscan.utils

import ru.hse.generated.models.ImageRequestStatus


val LABEL_TRANSCRIPTIONS = listOf(
    "пусто",
    "бампер задний",
    "бампер передний",
    "дверь задняя левая",
    "дверь задняя правая",
    "дверь передняя левая",
    "дверь передняя правая",
    "капот",
    "колесо",
    "крыло заднее левое",
    "крыло заднее правое",
    "крыло переднее левое",
    "крыло переднее правое",
    "крыша",
    "крышка багажника",
    "лобовое стекло",
    "заднее стекло",
    "антенна",
    "болты крепления диска колеса",
    "заглушка буксировочного крюка переднего бампера",
    "заглушка для диска колеса",
    "крышка бокового зеркала левая",
    "крышка бокового зеркала правая",
    "левая щётка стеклоочистителя лобового стекла",
    "левый поводок стеклоочистителя лобового стекла",
    "молдинг стекла задней двери левый наружный",
    "молдинг стекла задней двери правый наружный",
    "молдинг стекла передней двери левый наружный",
    "молдинг стекла передней двери правый наружный",
    "ниппель для колеса",
    "повторитель",
    "правая щётка стеклоочистителя лобового стекла",
    "правый поводок стеклоочистителя лобового стекла",
    "противотуманная фара левая (ПТФ)",
    "противотуманная фара правая (ПТФ)",
    "решетка стеклоочистителя (накладка жабо)",
    "ручка двери",
    "фонарь катафот левый в задний бампер",
    "фонарь катафот правый в задний бампер",
    "шильдик марки авто",
    "эмблема",
    "боковое зеркало левое",
    "боковое зеркало правое",
    "брызговик задний левый",
    "брызговик задний правый",
    "брызговик передний левый",
    "брызговик передний правый",
    "диск колеса",
    "крышка люка бензобака",
    "молдинг заднего бампера центральный",
    "молдинг зеркала левый",
    "молдинг зеркала правый",
    "накладка на порог левая",
    "накладка на порог правая",
    "накладка центральной стойки левая наружная",
    "накладка центральной стойки правая наружная",
    "подкрылок задний левый",
    "подкрылок задний правый",
    "подкрылок передний левый",
    "подкрылок передний правый",
    "рамка госномера",
    "решетка бампера центральная",
    "решетка переднего бампера под ПТФ левая",
    "решетка переднего бампера под ПТФ правая",
    "решетка радиатора",
    "стекло задней двери левое",
    "стекло задней двери левое (форточка)",
    "стекло задней двери правое",
    "стекло задней двери правое (форточка)",
    "стекло передней двери левое",
    "стекло передней двери правое",
    "фара передняя левая",
    "фара передняя правая",
    "фонарь задний левый",
    "фонарь задний правый",
)


val DAMAGE_LEVEL_TRANSCRIPTIONS = listOf(
    "нет повреждений",
    "дефект",
    "повреждение",
)

val STATUS_TRANSCRIPTION = mapOf(
    ImageRequestStatus.DONE to "Запрос успешно завершен",
    ImageRequestStatus.IN_PROGRESS to "Запрос в процессе обработки",
    ImageRequestStatus.FAILED to "Запрос завершен с ошибкой",
)

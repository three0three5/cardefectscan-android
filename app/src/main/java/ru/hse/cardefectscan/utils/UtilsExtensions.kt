package ru.hse.cardefectscan.utils

object UtilsExtensions {
    fun String.notBlank(): String? {
        if (this == "") return null
        return this
    }
}
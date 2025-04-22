package ru.hse.cardefectscan.domain.usecase

import android.content.Context
import android.text.format.Formatter
import java.io.File

class SettingsUseCase(
    private val context: Context,
) {
    fun getCacheSize(): String {
        val cacheDir = context.cacheDir
        val size = getFolderSize(cacheDir)
        return Formatter.formatShortFileSize(context, size)
    }

    fun clearCache() {
        context.cacheDir.deleteRecursively()
    }

    private fun getFolderSize(dir: File?): Long {
        if (dir == null || !dir.exists()) return 0
        return dir.walkBottomUp().filter { it.isFile }.map { it.length() }.sum()
    }
}
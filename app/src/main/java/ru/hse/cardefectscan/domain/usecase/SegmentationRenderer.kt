package ru.hse.cardefectscan.domain.usecase

import androidx.compose.ui.graphics.Color
import coil3.Image

class SegmentationRenderer {
    val colorSet = listOf(
        Color(0xFFFFFFFF),
        Color(0xFF0059FF),
        Color(0xFF26FF00),
        Color(0xFFFFF200),
        Color(0xFFFF8400),
        Color(0xFF8000FF),
        Color(0xFFFF0000),
        Color(0xFF162E4E),
        Color(0xFFB45A8B),
        Color(0xFF0059FF),
        Color(0xFF00A86B),
        Color(0xFF8A2BE2),
        Color(0xFFFF69B4),
        Color(0xFF00FFFF),
        Color(0xFF556B2F),
        Color(0xFF4682B4),
        Color(0xFFDC143C),
        Color(0xFFD2691E),
        Color(0xFFDAA520)
    )

    fun renderResult(
        original: Image,
        mask: Image,
        alpha: Double = 0.5,
    ): Image {
        val colorized = colorizeMask(mask)
        val blended = alphaBlending(original, mask, alpha)
        return blended
    }

    private fun colorizeMask(mask: Image): Image {
        return mask
    }

    private fun alphaBlending(
        img: Image,
        layer: Image,
        alpha: Double = 0.5,
    ): Image {
        return img
    }
}
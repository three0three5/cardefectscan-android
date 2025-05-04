package ru.hse.cardefectscan.domain.usecase

import android.graphics.Bitmap

class ResultUseCase(
    private val segmentationRenderer: SegmentationRenderer = SegmentationRenderer(),
) {
    fun generateColor(label: Int) = segmentationRenderer.generateColor(label)

    fun getRenderedImage(
        original: Bitmap,
        result: Bitmap,
        alpha: Float,
    ): Bitmap = segmentationRenderer.renderResult(
        original,
        result,
        alpha,
    )
}
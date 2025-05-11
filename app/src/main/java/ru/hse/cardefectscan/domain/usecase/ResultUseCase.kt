package ru.hse.cardefectscan.domain.usecase

import android.graphics.Bitmap

class ResultUseCase(
    private val segmentationRenderer: SegmentationRenderer = SegmentationRenderer(),
) {
    fun generateColor(segment: Int, damage: Int) = segmentationRenderer.generateColor(segment, damage)

    fun getRenderedImageWithLabels(
        original: Bitmap,
        result: Bitmap,
        alpha: Float,
    ): Pair<Bitmap, Set<Pair<Int, Int>>> = segmentationRenderer.renderResult(
        original,
        result,
        alpha,
    )
}
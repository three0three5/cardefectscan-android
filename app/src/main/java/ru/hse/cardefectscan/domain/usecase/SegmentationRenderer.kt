package ru.hse.cardefectscan.domain.usecase

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import ru.hse.cardefectscan.utils.DAMAGE_LEVEL_TRANSCRIPTIONS

class SegmentationRenderer {
    fun renderResult(
        original: Bitmap,
        mask: Bitmap,
        alpha: Float = 0.5f
    ): Pair<Bitmap, Set<Pair<Int, Int>>> {
        val colorizedWithPair = colorizeMask(mask)
        return Pair(alphaBlending(original, colorizedWithPair.first, alpha), colorizedWithPair.second)
    }

    fun generateColor(segment: Int, damage: Int): Int {
        if (segment == 0 && damage == 0) return Color.TRANSPARENT
        val colorValue = segment * DAMAGE_LEVEL_TRANSCRIPTIONS.size + damage
        val hue = (colorValue * 29) % 360
        val hsv = floatArrayOf(hue.toFloat(), 0.75f, 0.90f)
        return Color.HSVToColor(hsv)
    }

    private fun colorizeMask(mask: Bitmap): Pair<Bitmap, Set<Pair<Int, Int>>> {
        val readableMask = if (mask.config == Bitmap.Config.HARDWARE) {
            mask.copy(Bitmap.Config.ARGB_8888, false)
        } else {
            mask
        }

        val w = readableMask.width
        val h = readableMask.height
        val out = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val labels = mutableSetOf<Pair<Int, Int>>()

        for (y in 0 until h) {
            for (x in 0 until w) {
                val pixel = readableMask.getPixel(x, y)
                val segment = Color.red(pixel)
                val damage = Color.green(pixel)
                val color = generateColor(segment, damage)
                labels.add(Pair(segment, damage))
                out.setPixel(x, y, color)
            }
        }
        return Pair(out, labels)
    }

    private fun alphaBlending(
        original: Bitmap,
        overlay: Bitmap,
        alpha: Float,
    ): Bitmap {
        val result = original.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(result)
        val paint = Paint().apply {
            this.alpha = (alpha.coerceIn(0f, 1f) * 255).toInt()
        }
        canvas.drawBitmap(overlay, 0f, 0f, paint)
        return result
    }
}

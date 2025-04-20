package ru.hse.cardefectscan.domain.usecase

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.SparseArray

class SegmentationRenderer {

    /**
     * Рендерит результат сегментации: накладывает цветную маску на оригинал.
     *
     * @param original  исходное RGB-изображение
     * @param mask      маска сегментации (где значение канала R = лейбл 0…255)
     * @param alpha     прозрачность маски (0f — невидима, 1f — полностью непрозрачна)
     * @return          новый Bitmap с наложенной маской
     */
    fun renderResult(
        original: Bitmap,
        mask: Bitmap,
        alpha: Float = 0.5f
    ): Bitmap {
        val colorized = colorizeMask(mask)
        return alphaBlending(original, colorized, alpha)
    }

    fun generateColor(label: Int): Int {
        val hue = (label * 137) % 360
        val hsv = floatArrayOf(hue.toFloat(), 0.75f, 0.90f)
        return Color.HSVToColor(hsv)
    }

    private fun colorizeMask(mask: Bitmap): Bitmap {
        val w = mask.width
        val h = mask.height
        val out = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)

        val labelToColor = SparseArray<Int>()

        for (y in 0 until h) {
            for (x in 0 until w) {
                val color = when (val label = Color.red(mask.getPixel(x, y))) {
                    0 -> Color.TRANSPARENT
                    else -> labelToColor[label] ?: generateColor(label).also {
                        labelToColor.put(
                            label,
                            it
                        )
                    }
                }
                out.setPixel(x, y, color)
            }
        }
        return out
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

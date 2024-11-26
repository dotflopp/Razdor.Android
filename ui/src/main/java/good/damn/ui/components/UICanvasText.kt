package good.damn.ui.components

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import androidx.annotation.ColorInt

class UICanvasText
: UICanvas {

    @setparam:ColorInt
    @get:ColorInt
    var color: Int
        get() = mPaintText.color
        set(v) {
            mPaintText.color = v
        }

    var typeface: Typeface?
        get() = mPaintText.typeface
        set(v) {
            mPaintText.typeface = v
        }

    var textSize: Float
        get() = mPaintText.textSize
        set(v) {
            mPaintText.textSize = v
        }

    var alpha: Float
        get() = mPaintText.alpha / 255.toFloat()
        set(v) {
            mPaintText.alpha = (v * 255).toInt()
        }

    var text: String? = null

    var x = 0f
    var y = 0f

    private val mPaintText = Paint()

    override fun draw(
        canvas: Canvas
    ) {
        text?.apply {
            canvas.drawText(
                this,
                x,
                y,
                mPaintText
            )
        }
    }

    fun measureText() = text?.run {
        mPaintText.measureText(this)
    } ?: 0.0f

    fun center(
        width: Float,
        height: Float
    ) {
        text?.apply {
            x = (width - mPaintText.measureText(this)) * 0.5f
            y = (height + mPaintText.textSize) * 0.5f
        }
    }
}
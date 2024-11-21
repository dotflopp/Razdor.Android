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

    var typeface: Typeface
        get() = mPaintText.typeface
        set(v) {
            mPaintText.typeface = v
        }

    var textSize: Float
        get() = mPaintText.textSize
        set(v) {
            mPaintText.textSize = v
        }

    var text: String? = null

    private var mx = 0f
    private var my = 0f

    private val mPaintText = Paint()

    override fun draw(
        canvas: Canvas
    ) {
        text?.apply {
            canvas.drawText(
                this,
                mx,
                my,
                mPaintText
            )
        }
    }

    override fun layout(
        width: Float,
        height: Float
    ) {
        text?.apply {
            mx = (width - mPaintText.measureText(this)) * 0.5f
            my = (height + mPaintText.textSize) * 0.5f
        }
    }
}
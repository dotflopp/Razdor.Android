package good.damn.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import good.damn.ui.components.UICanvasText
import good.damn.ui.theme.UITheme

class UIButton(
    context: Context
): UIView(
    context
) {

    var cornerRadius = 15f

    @setparam:ColorInt
    @get:ColorInt
    var textColor: Int
        get() = mCanvasText.color
        set(v) {
            mCanvasText.color = v
        }

    var typeface: Typeface
        get() = mCanvasText.typeface
        set(v) {
            mCanvasText.typeface = v
        }

    var textSizeFactor = 0.2f

    var text: String?
        get() = mCanvasText.text
        set(v) {
            mCanvasText.text = v
        }

    private val mPaintBackground = Paint()
    private val mRect = RectF()

    private val mCanvasText = UICanvasText()

    override fun setBackgroundColor(
        color: Int
    ) {
        mPaintBackground.color = color
    }

    override fun onLayout(
        changed: Boolean,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ) {
        super.onLayout(
            changed, left,
            top, right, bottom
        )

        mRect.left = 0f
        mRect.top = 0f
        mRect.right = width.toFloat()
        mRect.bottom = height.toFloat()

        mCanvasText.textSize = height * textSizeFactor

        mCanvasText.layout(
            mRect.right,
            mRect.bottom
        )
    }

    override fun onDraw(
        canvas: Canvas
    ) = canvas.run {

        drawRoundRect(
            mRect,
            cornerRadius,
            cornerRadius,
            mPaintBackground
        )

        mCanvasText.draw(
            canvas
        )
    }

    override fun applyTheme(
        theme: UITheme
    ) {
        mPaintBackground.color = theme.colorButton
        mCanvasText.color = theme.colorButtonText
    }

    override fun onTouchEvent(
        event: MotionEvent?
    ): Boolean {
        return true
    }
}
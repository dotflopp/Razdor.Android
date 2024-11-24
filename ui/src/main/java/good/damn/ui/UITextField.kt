package good.damn.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import androidx.annotation.ColorInt
import good.damn.ui.components.UICanvasText
import good.damn.ui.extensions.isOutsideView
import good.damn.ui.theme.UITheme

class UITextField(
    context: Context
): UIView(
    context
) {

    companion object {
        private val TAG = UITextField::class.simpleName
    }

    var hint: String?
        get() = mCanvasHint.text
        set(v) {
            mCanvasHint.text = v
        }

    @setparam:ColorInt
    @get:ColorInt
    var textColor: Int
        get() = mCanvasHint.color
        set(v) {
            mCanvasHint.color = v
        }

    private val mCanvasHint = UICanvasText()

    private var mHintSizeInitial = 0f
    private var mHintYInitial = 0f

    init {
        background = null
        animationTouchDown = null
        animationTouchUp = null

        mPaintBackground.apply {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }

    }

    override fun onLayout(
        changed: Boolean,
        left: Int, top: Int,
        right: Int, bottom: Int
    ) {
        super.onLayout(
            changed,
            left, top,
            right, bottom
        )

        val rootWidth = width.toFloat()
        val rootHeight = height.toFloat()

        mHintSizeInitial = 0.2f * rootHeight
        mCanvasHint.textSize = mHintSizeInitial

        (0.03f * rootHeight).let { strokeWidth ->
            mPaintBackground.strokeWidth = strokeWidth

            mRect.left += strokeWidth
            mRect.top += strokeWidth + mCanvasHint.textSize
            mRect.right -= strokeWidth
            mRect.bottom -= strokeWidth
        }

        mCanvasHint.apply {
            x = rootWidth * 0.1f
            y = (rootHeight +
                mRect.top +
                mCanvasHint.textSize * 0.5f
            ) * 0.5f

            mHintYInitial = y
        }
    }

    override fun onDraw(
        canvas: Canvas
    ) = canvas.run {
        drawRoundRect(
            mRect,
            mCornerRadius,
            mCornerRadius,
            mPaintBackground
        )

        mCanvasHint.draw(
            canvas
        )
    }

    override fun onTouchEvent(
        event: MotionEvent?
    ): Boolean {

        if (event == null) {
            return false
        }

        when (event.action) {

            MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_UP -> {
                if (isOutsideView(
                    event.x,
                    event.y
                )) {
                    return true
                }

                mCanvasHint.apply {
                    if (textSize == mHintSizeInitial) {
                        textSize = mHintSizeInitial * 0.4f
                        y = mRect.top + textSize * 0.5f
                    } else {
                        textSize = mHintSizeInitial
                        y = mHintYInitial
                    }
                }

                invalidate()
            }
        }

        return true
    }

    override fun applyTheme(
        theme: UITheme
    ) {
        mCanvasHint.color = theme.colorText
        mPaintBackground.color = theme.colorText
    }

}
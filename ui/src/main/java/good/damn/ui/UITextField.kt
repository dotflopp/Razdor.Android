package good.damn.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.TypedValue
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatEditText
import good.damn.ui.components.UICanvasText
import good.damn.ui.extensions.isOutsideView

class UITextField(
    context: Context
): AppCompatEditText(
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
    var tintColor: Int
        get() = mCanvasHint.color
        set(v) {
            mCanvasHint.color = v
            setTextColor(v)
            setLinkTextColor(v)
            mPaintStroke.color = v
        }

    private val mPaintStroke = Paint().apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val mCanvasHint = UICanvasText()
    private val mRectHint = RectF()

    private var mHintSizeInitial = 0f
    private var mHintYInitial = 0f

    private var mCornerRadius = 60f

    private val mPaintBackText = Paint().apply {
        color = 0xff000315.toInt()
    }

    private val mRect = RectF()

    init {
        background = null
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

        setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            mHintSizeInitial
        )

        (0.03f * rootHeight).let { strokeWidth ->
            mPaintStroke.strokeWidth = strokeWidth

            mRect.left = strokeWidth
            mRect.top = strokeWidth + mCanvasHint.textSize
            mRect.right = rootWidth - strokeWidth
            mRect.bottom = rootHeight - strokeWidth
        }

        mCanvasHint.apply {
            x = rootWidth * 0.1f
            y = (rootHeight +
                mRect.top +
                mCanvasHint.textSize * 0.5f
            ) * 0.5f

            mHintYInitial = y

            setPadding(
                x.toInt(),
                mRect.top.toInt(),
                0,0
            )
        }
    }

    override fun onDraw(
        canvas: Canvas
    ) = canvas.run {
        super.onDraw(
            this
        )
        drawRoundRect(
            mRect,
            mCornerRadius,
            mCornerRadius,
            mPaintStroke
        )

        if (mCanvasHint.textSize != mHintSizeInitial) {
            drawRect(
                mRectHint,
                mPaintBackText
            )
        }

        mCanvasHint.draw(
            canvas
        )
    }

    override fun onFocusChanged(
        focused: Boolean,
        direction: Int,
        previouslyFocusedRect: Rect?
    ) {

        mCanvasHint.apply {

            if (focused) {

                textSize = mHintSizeInitial * 0.85f
                y = mRect.top + textSize * 0.5f

                val margin = width * 0.02f

                mRectHint.left = x - margin
                mRectHint.top = 0f

                mRectHint.right = x + measureText() + margin
                mRectHint.bottom = y + textSize
                return@apply
            }

            if (this@UITextField.text?.isBlank() != false) {
                textSize = mHintSizeInitial
                y = mHintYInitial
            }
        }

        super.onFocusChanged(
            focused,
            direction,
            previouslyFocusedRect
        )
    }

    /*override fun applyTheme(
        theme: UITheme
    ) {
        mCanvasHint.color = theme.colorText
        mPaintBackground.color = theme.colorText
    }*/

}
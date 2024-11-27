package good.damn.ui.textfield

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.text.InputType
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatEditText
import good.damn.ui.components.UICanvasText
import good.damn.ui.interfaces.UIThemable
import good.damn.ui.theme.UITheme

class UITextField(
    context: Context
): AppCompatEditText(
    context
), UIThemable {

    companion object {
        private val TAG = UITextField::class.simpleName
    }

    var hint: String?
        get() = mCanvasHint.text
        set(v) {
            mCanvasHint.text = v
        }

    var subhint: String?
        get() = mCanvasSubhint.text
        set(v) {
            mCanvasSubhint.text = if (v?.isBlank() == false)
                "• $v"
            else null
        }

    var strokeWidth: Float
        get() = mPaintStroke.strokeWidth
        set(v) {
            mPaintStroke.strokeWidth = v
        }

    var cornerRadiusFactor = 0.25f

    var typefaceSubhint: Typeface?
        get() = mCanvasSubhint.typeface
        set(v) {
            mCanvasSubhint.typeface = v
        }

    var typefaceHint: Typeface?
        get() = mCanvasHint.typeface
        set(v) {
            mCanvasHint.typeface = v
        }

    @get:ColorInt
    @setparam:ColorInt
    var tintColor: Int
        get() = mCanvasHint.color
        set(v) {
            mCanvasHint.color = v
            mCanvasSubhint.color = v
            mPaintStroke.color = v
        }

    private val mPaintStroke = Paint().apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val mPaintBackText = Paint().apply {
        xfermode = PorterDuffXfermode(
            PorterDuff.Mode.CLEAR
        )
    }

    private val mCanvasHint = UICanvasText()
    private val mCanvasSubhint = UICanvasText()

    private val mRectHint = RectF()
    private val mRect = RectF()

    private var mHasError = false

    private var mCornerRadius = 0f

    private var mColorTintFocus = 0
    private var mColorTintFocusNo = 0

    private val mAnimator = UITextFieldAnimator(
        this,
        mCanvasHint,
        mRectHint,
        mCanvasSubhint,
        mRect
    )

    init {
        background = null
        maxLines = 1

        // It needs to work with transparent color drawing
        // see drawRect(mRectHint)
        setLayerType(
            View.LAYER_TYPE_HARDWARE,
            null
        )

        inputType = InputType.TYPE_CLASS_TEXT
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

        mPaintStroke.strokeWidth = strokeWidth

        mRect.left = strokeWidth
        mRect.top = strokeWidth * 1.5f + mCanvasHint.textSize
        mRect.right = rootWidth - strokeWidth
        mRect.bottom = rootHeight - strokeWidth

        mCornerRadius = height * cornerRadiusFactor

        mAnimator.layout(
            rootWidth,
            rootHeight
        )
    }

    override fun onDraw(
        canvas: Canvas
    ) = canvas.run {
        super.onDraw(
            this
        )

        translate(
            scrollX.toFloat(),
            scrollY.toFloat()
        )

        drawRoundRect(
            mRect,
            mCornerRadius,
            mCornerRadius,
            mPaintStroke
        )

        drawRect(
            mRectHint,
            mPaintBackText
        )

        mCanvasHint.draw(
            canvas
        )

        save()
        clipRect(
            mRectHint
        )

        mCanvasSubhint.draw(
            canvas
        )

        restore()
    }

    override fun onFocusChanged(
        focused: Boolean,
        direction: Int,
        previouslyFocusedRect: Rect?
    ) = mCanvasHint.run {

        if (mHasError) {
            if (focused) {
                focusColor()
            } else {
                focusNoColor()
            }
            mHasError = false
        }

        if (isEnabled) {
            if (focused) {
                focusColor()
                if (this@UITextField.text.isNullOrBlank()) {
                    mAnimator.focus(
                        width.toFloat()
                    )
                }
            } else if (this@UITextField.text.isNullOrBlank()) {
                focusNoColor()
                mAnimator.focusNo()
            }
        }

        super.onFocusChanged(
            focused,
            direction,
            previouslyFocusedRect
        )
    }

    override fun applyTheme(
        theme: UITheme
    ) {
        mColorTintFocus = theme.colorAccentTextField
        mColorTintFocusNo = theme.colorTextEditUnfocused

        if (this@UITextField.text?.isBlank() != false) {
            focusNoColor()
        } else {
            focusColor()
        }

        setTextColor(
            theme.colorText
        )
    }

    final override fun setTypeface(
        tf: Typeface?
    ) {
        // This shit calls inside AppCompatEditText constructor
        // so mCanvasHint is null and IDE is bitching with warning
        // check mCanvasHint == null
        tf ?: return
        if (mCanvasHint == null) {
            return
        }
        mCanvasHint.typeface = tf
        mCanvasSubhint.typeface = tf
        super.setTypeface(tf)
    }

    fun error(
        theme: UITheme
    ) {
        mCanvasHint.color = theme.colorError
        mPaintStroke.color = theme.colorError
        mCanvasSubhint.color = theme.colorError

        mHasError = true
        invalidate()
    }

    private inline fun focusNoColor() {
        mCanvasHint.color = mColorTintFocusNo
        mPaintStroke.color = mColorTintFocusNo
        mCanvasSubhint.color = mColorTintFocusNo
    }


    private inline fun focusColor() {
        mCanvasHint.color = mColorTintFocus
        mPaintStroke.color = mColorTintFocus
        mCanvasSubhint.color = mColorTintFocus
    }
}
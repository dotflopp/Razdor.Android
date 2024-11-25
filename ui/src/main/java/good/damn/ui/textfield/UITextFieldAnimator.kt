package good.damn.ui.textfield

import android.animation.ValueAnimator
import android.graphics.RectF
import android.util.TypedValue
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator
import good.damn.ui.components.UICanvasText

class UITextFieldAnimator(
    private val textField: UITextField,
    private val canvasHint: UICanvasText,
    private val mRectHint: RectF,
    private val mRect: RectF
): ValueAnimator.AnimatorUpdateListener {

    private val mAnimator = ValueAnimator().apply {
        duration = 150
        interpolator = OvershootInterpolator()

        addUpdateListener(
            this@UITextFieldAnimator
        )
    }

    private var mFromY = 0f
    private var mToY = 0f

    private var mFromTextSize = 0f
    private var mToTextSize = 0f

    private var mWidthText = 0f
    private var marginText = 0f

    private var mHintSizeInitial = 0f
    private var mHintSizeSmall = 0f

    private var mHintYInitial = 0f
    private var mHintYSmall = 0f

    fun layout(
        width: Float,
        height: Float
    ) {
        mHintSizeInitial = 0.2f * height
        mHintSizeSmall = mHintSizeInitial * 0.85f

        mHintYSmall = mRect.top + mHintSizeSmall * 0.5f

        canvasHint.textSize = mHintSizeInitial

        textField.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            mHintSizeInitial
        )

        marginText = width * 0.02f

        canvasHint.apply {
            x = width * 0.1f
            y = (height +
                mRect.top +
                canvasHint.textSize * 0.5f
            ) * 0.5f

            mHintYInitial = y

            textField.setPadding(
                x.toInt(),
                mRect.top.toInt(),
                0,0
            )
        }
    }

    fun focus(
        width: Float
    ) = canvasHint.run {

        mFromTextSize = mHintSizeInitial
        mToTextSize = mHintSizeSmall

        mFromY = mHintYInitial
        mToY = mHintYSmall

        mWidthText = measureText() + marginText

        mRectHint.left = x - marginText
        mRectHint.top = 0f

        mRectHint.right = x + mWidthText
        mRectHint.bottom = mRect.top + mHintSizeSmall

        mAnimator.apply {
            setFloatValues(
                0.0f, 1.0f
            )
            start()
        }
    }

    fun focusNo() = mAnimator.run {
        setFloatValues(
            1.0f, 0.0f
        )
        start()
    }

    override fun onAnimationUpdate(
        animation: ValueAnimator
    ) {
        val f = animation.animatedValue as Float

        canvasHint.apply {
            mRectHint.left = x - marginText * f
            mRectHint.right = x + mWidthText * f
            textSize = mFromTextSize + (mToTextSize - mFromTextSize) * f
            y = mFromY + (mToY - mFromY) * f
        }
        textField.invalidate()
    }

}
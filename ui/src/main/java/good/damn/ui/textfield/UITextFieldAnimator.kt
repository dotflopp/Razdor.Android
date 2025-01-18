package good.damn.ui.textfield

import android.animation.ValueAnimator
import android.graphics.RectF
import android.util.TypedValue
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.view.animation.OvershootInterpolator
import good.damn.ui.components.UICanvasText

class UITextFieldAnimator(
    private val textField: UITextField,
    private val canvasHint: UICanvasText,
    private val mRectHint: RectF,
    private val canvasSubhint: UICanvasText,
    private val mRect: RectF
): ValueAnimator.AnimatorUpdateListener {

    private val mAnimator = ValueAnimator().apply {
        duration = 350
        addUpdateListener(
            this@UITextFieldAnimator
        )
    }

    private val mInterpolatorStart = OvershootInterpolator(0.9f)
    private val mInterpolatorEnd = AccelerateDecelerateInterpolator()

    private var mFromY = 0f
    private var mToY = 0f

    private var mFromTextSize = 0f
    private var mToTextSize = 0f

    private var mWidthHint = 0f
    private var mWidthSubhint = 0f
    private var marginHint = 0f

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

        canvasSubhint.textSize = mHintSizeSmall

        canvasHint.textSize = mHintSizeInitial

        textField.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            mHintSizeInitial
        )

        marginHint = width * 0.02f

        canvasHint.apply {
            x = width * 0.06f
            y = (height +
                mRect.top +
                canvasHint.textSize * 0.5f
            ) * 0.5f

            mHintYInitial = y

            textField.setPadding(
                x.toInt(),
                mRect.top.toInt(),
                x.toInt(),
                0
            )
        }

        canvasSubhint.y = mHintYSmall
    }

    fun focus(
        width: Float
    ) = canvasHint.run {

        mFromTextSize = mHintSizeInitial
        mToTextSize = mHintSizeSmall

        mFromY = mHintYInitial
        mToY = mHintYSmall

        mWidthHint = measureText() + marginHint
        canvasSubhint.x = x + mWidthHint

        mWidthSubhint = canvasSubhint.measureText() + marginHint

        mRectHint.left = x - marginHint
        mRectHint.top = 0f

        mRectHint.right = x + mWidthHint
        mRectHint.bottom = mRect.top + mHintSizeSmall

        mWidthHint += mWidthSubhint

        mAnimator.apply {
            interpolator = mInterpolatorStart
            setFloatValues(
                0.0f, 1.0f
            )
            start()
        }
    }

    fun focusNo() = mAnimator.run {
        interpolator = mInterpolatorEnd
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
            mRectHint.left = x - marginHint * f
            mRectHint.right = x + mWidthHint * f
            textSize = mFromTextSize + (mToTextSize - mFromTextSize) * f
            y = mFromY + (mToY - mFromY) * f
        }
        textField.invalidate()
    }

}
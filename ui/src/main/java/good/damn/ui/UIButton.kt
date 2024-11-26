package good.damn.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import good.damn.ui.components.UICanvas
import good.damn.ui.components.UICanvasText
import good.damn.ui.theme.UITheme

open class UIButton(
    context: Context
): UIView(
    context
) {

    @setparam:ColorInt
    @get:ColorInt
    var textColor: Int
        get() = mCanvasText.color
        set(v) {
            mCanvasText.color = v
            mCanvasText2.color = v
        }

    var typeface: Typeface
        get() = mCanvasText.typeface
        set(v) {
            mCanvasText.typeface = v
            mCanvasText2.typeface = v
        }

    var textSizeFactor = 0.2f

    var text: String?
        get() = mCanvasText.text
        set(v) {
            mCanvasText.text = v
            mCanvasText2.text = v
        }

    private val mAnimator = ValueAnimator().apply {
        duration = 100
        interpolator = AccelerateDecelerateInterpolator()

        setFloatValues(
            0.0f, 1.0f
        )

        addUpdateListener {
            onAnimateText(it)
        }
    }

    private val mCanvasText = UICanvasText()
    private val mCanvasText2 = UICanvasText()

    private var mTextY = 0f

    private var mCurrentFraction = 0f
    private var mCurrentFractionInverse = 0f

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

        mCanvasText.apply {
            textSize = height * textSizeFactor
            center(
                mRect.right,
                mRect.bottom
            )
            mTextY = y
        }

        mCanvasText2.apply {
            textSize = mCanvasText.textSize
            center(
                mRect.right,
                mRect.bottom
            )
        }
    }

    override fun onDraw(
        canvas: Canvas
    ) {
        super.onDraw(
            canvas
        )

        if (mAnimator.isRunning) {
            mCanvasText2.draw(
                canvas
            )
        }

        mCanvasText.draw(
            canvas
        )
    }

    override fun applyTheme(
        theme: UITheme
    ) {
        mPaintBackground.color = theme.colorBackgroundButton
        mCanvasText.color = theme.colorTextButton
    }


    fun changeTextAnimated(
        text: String
    ) {
        mCanvasText2.apply {
            y = mTextY
            this.text = mCanvasText.text
            center(
                mRect.right,
                mRect.bottom
            )
        }

        mCanvasText.apply {
            this.text = text
            center(
                mRect.right,
                mRect.bottom
            )
        }

        mAnimator.start()
    }

    private inline fun onAnimateText(
        animator: ValueAnimator
    ) {
        mCurrentFraction = animator.animatedValue as Float
        mCurrentFractionInverse = 1.0f - mCurrentFraction

        mCanvasText2.y = mTextY * mCurrentFractionInverse
        mCanvasText.y = height + mCanvasText.textSize - mTextY * mCurrentFraction

        invalidate()
    }
}
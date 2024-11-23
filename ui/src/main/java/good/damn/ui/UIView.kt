package good.damn.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import good.damn.ui.animation.UIAnimationScale
import good.damn.ui.animation.misc.UIAnimation
import good.damn.ui.animation.misc.UIAnimationUpdate
import good.damn.ui.extensions.isOutsideView
import good.damn.ui.theme.UITheme

abstract class UIView(
    context: Context
): View(
    context
), ValueAnimator.AnimatorUpdateListener {
    
    companion object {
        private val TAG = UIView::class.simpleName
    }

    var cornerRadiusFactor = 0.2f
    var scale = 1.0f

    var animationTouchDown: UIAnimation? = UIAnimationScale(
        1.0f,
        0.85f,
        this
    )

    var animationTouchUp: UIAnimation? = UIAnimationScale(
        0.85f,
        1.0f,
        this
    )

    protected val mPaintBackground = Paint()
    protected val mRect = RectF()
    protected var mCornerRadius = 0f

    private var mCurrentAnimation: UIAnimation? = null

    private var mOnClick: OnClickListener? = null

    private val mAnimator = ValueAnimator().apply {
        duration = 250
        interpolator = AccelerateDecelerateInterpolator()
        setFloatValues(
            0.0f, 1.0f
        )

        addUpdateListener(
            this@UIView
        )
    }

    final override fun setBackgroundColor(
        color: Int
    ) {
        mPaintBackground.color = color
    }

    final override fun setOnClickListener(
        l: OnClickListener?
    ) {
        mOnClick = l
    }

    override fun onDraw(
        canvas: Canvas
    ) = canvas.run {

        scale(
            scale,
            scale,
            width * 0.5f,
            height * 0.5f
        )

        if (mPaintBackground.color == 0) {
            return@run
        }

        drawRoundRect(
            mRect,
            mCornerRadius,
            mCornerRadius,
            mPaintBackground
        )
    }

    override fun onLayout(
        changed: Boolean,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ) {
        super.onLayout(
            changed,
            left, top,
            right, bottom
        )

        mCornerRadius = height * cornerRadiusFactor

        mRect.left = 0f
        mRect.top = 0f
        mRect.right = width.toFloat()
        mRect.bottom = height.toFloat()
    }

    override fun onTouchEvent(
        event: MotionEvent?
    ): Boolean {

        if (event == null) {
            return false
        }

        when (event.action) {

            MotionEvent.ACTION_DOWN -> {
                animationTouchDown?.apply {
                    mCurrentAnimation = this
                    mAnimator.start()
                }
            }

            MotionEvent.ACTION_CANCEL -> {
                animationTouchUp?.apply {
                    mCurrentAnimation = this
                    mAnimator.start()
                }
            }

            MotionEvent.ACTION_UP -> {
                animationTouchUp?.apply {
                    mCurrentAnimation = this
                    mAnimator.start()
                }
                if (isOutsideView(
                    event.x,
                    event.y
                )) {
                    return true
                }

                mOnClick?.onClick(
                    this
                )
            }


        }

        return true
    }


    abstract fun applyTheme(
        theme: UITheme
    )

    override fun onAnimationUpdate(
        animation: ValueAnimator
    ) {
        mCurrentAnimation?.updateAnimation(
            animation.animatedValue as Float
        )
        invalidate()
    }
}
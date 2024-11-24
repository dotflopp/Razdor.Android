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
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import good.damn.ui.animation.UIAnimationScale
import good.damn.ui.animation.misc.UIAnimation
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
        0.9f,
        100,
        AccelerateDecelerateInterpolator(),
        this
    )

    var animationTouchUp: UIAnimation? = UIAnimationScale(
        0.9f,
        1.0f,
        200,
        OvershootInterpolator(3.0f),
        this
    )

    protected val mPaintBackground = Paint()
    protected val mRect = RectF()
    protected var mCornerRadius = 0f

    private var mCurrentAnimation: UIAnimation? = null

    private var mOnClick: OnClickListener? = null

    private var mCurrentValueAnimation = 0.0f

    private val mAnimator = ValueAnimator().apply {
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
                startTouchAnimation(
                    animationTouchDown,
                    0.0f,
                    1.0f
                )
            }

            MotionEvent.ACTION_CANCEL -> {
                startTouchAnimation(
                    animationTouchUp,
                    0.0f,
                    1.0f
                )
            }

            MotionEvent.ACTION_UP -> {
                startTouchAnimation(
                    animationTouchUp,
                    1.0f-mCurrentValueAnimation,
                    1.0f
                )
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

    override fun onAnimationUpdate(
        animation: ValueAnimator
    ) {
        mCurrentValueAnimation = animation.animatedFraction
        mCurrentAnimation?.updateAnimation(
            animation.animatedValue as Float
        )
        invalidate()
    }

    private inline fun startTouchAnimation(
        animation: UIAnimation?,
        startValue: Float,
        endValue: Float
    ) = mAnimator.run {

        animation ?: return@run

        mCurrentAnimation = animation

        interpolator = animation.interpolator
        duration = animation.duration

        setFloatValues(
            startValue,
            endValue
        )
        start()
    }


    abstract fun applyTheme(
        theme: UITheme
    )
}
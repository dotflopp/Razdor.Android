package good.damn.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import android.view.View
import good.damn.ui.animation.UIAnimationGroup
import good.damn.ui.extensions.isOutsideView
import good.damn.ui.interfaces.UIThemable
import good.damn.ui.theme.UITheme

abstract class UIView(
    context: Context
): View(
    context
), ValueAnimator.AnimatorUpdateListener,
UIThemable {
    
    companion object {
        private val TAG = UIView::class.simpleName
    }

    var cornerRadiusFactor = 0.2f
        set(v) {
            field = v
            mCornerRadius = height * v
        }
    var scale = 1.0f

    var animationGroupTouchDown: UIAnimationGroup? = null
    var animationGroupTouchUp: UIAnimationGroup? = null

    var onClickDisabled: OnClickListener? = null

    var isClippedBounds = false

    protected val mPaintBackground = Paint()
    protected val mRect = RectF()
    protected var mCornerRadius = 0f

    private var mCurrentAnimationGroup: UIAnimationGroup? = null

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
        if (isClippedBounds) {
            clipRect(
                mRect
            )
        }

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

        if (!isEnabled) {
            onClickDisabled?.onClick(
                this
            )
            return false
        }

        when (
            event.action
        ) {

            MotionEvent.ACTION_DOWN -> {
                startTouchAnimation(
                    animationGroupTouchDown,
                    0.0f,
                    1.0f
                )
            }

            MotionEvent.ACTION_CANCEL -> {
                startTouchAnimation(
                    animationGroupTouchUp,
                    0.0f,
                    1.0f
                )
            }

            MotionEvent.ACTION_UP -> {
                startTouchAnimation(
                    animationGroupTouchUp,
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
        val f = animation.animatedValue as Float
        mCurrentAnimationGroup?.animations?.forEach {
            it.updateAnimationView(
                this,
                f
            )
        }
        invalidate()
    }

    private inline fun startTouchAnimation(
        group: UIAnimationGroup?,
        startValue: Float,
        endValue: Float
    ) = mAnimator.run {

        group ?: return@run

        mCurrentAnimationGroup = group

        interpolator = group.interpolator
        duration = group.duration

        setFloatValues(
            startValue,
            endValue
        )
        start()
    }
}
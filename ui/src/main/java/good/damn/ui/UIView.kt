package good.damn.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
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

    var scale = 1.0f

    var animationTouchDown: UIAnimation = UIAnimationScale(
        1.0f,
        0.85f,
        this
    )

    var animationTouchUp: UIAnimation = UIAnimationScale(
        0.85f,
        1.0f,
        this
    )

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


    final override fun setOnClickListener(
        l: OnClickListener?
    ) {
        mOnClick = l
    }

    override fun onTouchEvent(
        event: MotionEvent?
    ): Boolean {

        if (event == null) {
            return false
        }

        when (event.action) {

            MotionEvent.ACTION_DOWN -> {
                mCurrentAnimation = animationTouchDown
                mAnimator.start()
            }

            MotionEvent.ACTION_CANCEL -> {
                mCurrentAnimation = animationTouchUp
                mAnimator.start()
            }

            MotionEvent.ACTION_UP -> {
                mCurrentAnimation = animationTouchUp
                mAnimator.start()
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
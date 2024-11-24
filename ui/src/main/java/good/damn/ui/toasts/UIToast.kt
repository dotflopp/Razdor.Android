package good.damn.ui.toasts

import android.animation.ValueAnimator
import android.content.Context
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import good.damn.ui.theme.UITheme

abstract class UIToast(
    context: Context
): FrameLayout(
    context
), ValueAnimator.AnimatorUpdateListener {

    private val mAnimator = ValueAnimator().apply {
        interpolator = AccelerateDecelerateInterpolator()
        duration = 250
        addUpdateListener(
            this@UIToast
        )
    }

    private var mStartValue = 0.0f

    open fun show() = mAnimator.run {
        setFloatValues(
            mStartValue, 1.0f
        )

        start()
    }

    open fun hide() = mAnimator.run {
        setFloatValues(
            mStartValue, 0.0f
        )

        start()
    }

    abstract fun applyTheme(
        theme: UITheme
    )

    override fun onAnimationUpdate(
        animation: ValueAnimator
    ) {
        mStartValue = animation.animatedValue as Float
        alpha = mStartValue
    }

}
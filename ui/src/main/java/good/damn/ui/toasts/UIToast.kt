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
        duration = 450
        addUpdateListener(
            this@UIToast
        )
    }

    open fun show() = mAnimator.run {
        setFloatValues(
            0.0f, 1.0f
        )

        start()
    }

    open fun hide() = mAnimator.run {
        setFloatValues(
            1.0f, 0.0f
        )

        start()
    }

    abstract fun applyTheme(
        theme: UITheme
    )

    override fun onAnimationUpdate(
        animation: ValueAnimator
    ) {
        alpha = animation.animatedValue as Float
    }

}
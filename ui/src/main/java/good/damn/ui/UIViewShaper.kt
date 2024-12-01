package good.damn.ui

import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.graphics.Canvas
import android.view.View
import android.view.animation.OvershootInterpolator
import good.damn.ui.components.shapes.UICanvasShape
import good.damn.ui.interfaces.UIThemable
import good.damn.ui.theme.UITheme

class UIViewShaper(
    context: Context
): View(
    context
), UIThemable,
ValueAnimator.AnimatorUpdateListener {

    init {
        background = null
    }

    var shapes: Array<UICanvasShape>? = null

    private val mAnimator = ValueAnimator().apply {
        interpolator = OvershootInterpolator(0.9f)
        duration = 600

        setFloatValues(
            0.0f, 1.0f
        )

        addUpdateListener(
            this@UIViewShaper
        )
    }

    override fun applyTheme(
        theme: UITheme
    ) {
        shapes?.forEach {
            it.paint.color = theme.colorShape
        }
    }

    override fun onDraw(
        canvas: Canvas
    )  {
        shapes?.forEach {
            it.draw(
                canvas
            )
        }
    }

    override fun onAnimationUpdate(
        animation: ValueAnimator
    ) {
        tickAnimation(
            animation.animatedValue as Float
        )
        invalidate()
    }

    fun addUpdateListener(
        listener: AnimatorUpdateListener
    ) {
        mAnimator.addUpdateListener(
            listener
        )
    }

    fun setupPathAnimation(
        start: Float,
        end: Float
    ) {
        mAnimator.setFloatValues(
            start,
            end
        )
    }

    fun animateChange() {
        mAnimator.start()
    }

    private inline fun tickAnimation(
        t: Float
    ) {
        shapes?.forEach {
            it.tickAnimation(t)
        }
    }

}
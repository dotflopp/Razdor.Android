package good.damn.ui.interpolators

import android.view.animation.Interpolator
import kotlin.math.sin

class UIInterpolatorShake(
    private val period: Float
): Interpolator {

    companion object {
        private const val pi = Math.PI.toFloat()
    }
    override fun getInterpolation(
        input: Float
    ) = sin(input * pi * period) / pi

}
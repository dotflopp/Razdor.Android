package good.damn.ui.animation

import android.view.View
import android.view.animation.Interpolator
import good.damn.ui.UIView
import good.damn.ui.animation.misc.UIAnimation

class UIAnimationScale(
    private val from: Float,
    private val to: Float,
    duration: Long,
    interpolator: Interpolator,
    view: UIView
): UIAnimation(
    duration,
    interpolator,
    view
) {

    override fun updateAnimation(
        t: Float
    ) {
       view.scale = from + (to - from) * t
    }

}
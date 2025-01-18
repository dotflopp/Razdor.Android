package good.damn.ui.animation

import android.view.View
import android.view.animation.Interpolator
import good.damn.ui.UIView
import good.damn.ui.animation.misc.UIAnimationViewUpdate

class UIAnimationScale(
    private val from: Float,
    private val to: Float
): UIAnimationViewUpdate {
    override fun updateAnimationView(
        view: UIView,
        t: Float
    ) {
       view.scale = from + (to - from) * t
    }
}
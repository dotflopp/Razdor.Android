package good.damn.ui.animation

import good.damn.ui.UIView
import good.damn.ui.animation.misc.UIAnimationViewUpdate

class UIAnimationCornerRadius(
    private val from: Float,
    private val to: Float
): UIAnimationViewUpdate {

    override fun updateAnimationView(
        view: UIView,
        t: Float
    ) {
        view.cornerRadiusFactor = from + (to - from) * t
    }

}
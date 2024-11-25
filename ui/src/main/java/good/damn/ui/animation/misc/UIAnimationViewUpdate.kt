package good.damn.ui.animation.misc

import good.damn.ui.UIView

interface UIAnimationViewUpdate {

    fun updateAnimationView(
        view: UIView,
        t: Float
    )

}
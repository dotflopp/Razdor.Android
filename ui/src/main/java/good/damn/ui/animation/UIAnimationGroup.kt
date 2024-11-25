package good.damn.ui.animation

import android.view.animation.Interpolator
import good.damn.ui.animation.misc.UIAnimationViewUpdate

data class UIAnimationGroup(
    val animations: Array<UIAnimationViewUpdate>,
    val interpolator: Interpolator,
    val duration: Long
)
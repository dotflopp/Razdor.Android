package good.damn.ui.animation.misc

import android.view.animation.Interpolator
import good.damn.ui.UIView

abstract class UIAnimation(
    val duration: Long,
    val interpolator: Interpolator,
    val view: UIView
): UIAnimationUpdate
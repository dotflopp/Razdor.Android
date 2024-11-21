package good.damn.ui.extensions

import android.view.View

inline fun View.isOutsideView(
    x: Float,
    y: Float
) = x < 0f
 || y < 0f
 || x > width
 || y > height
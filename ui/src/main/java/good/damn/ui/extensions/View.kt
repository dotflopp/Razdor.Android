package good.damn.ui.extensions

import android.view.View
import android.view.View.MeasureSpec
import android.widget.TextView

inline fun View.isOutsideView(
    x: Float,
    y: Float
) = x < 0f
 || y < 0f
 || x > width
 || y > height
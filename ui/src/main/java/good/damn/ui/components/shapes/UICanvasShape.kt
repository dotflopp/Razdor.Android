package good.damn.ui.components.shapes

import android.graphics.Paint
import good.damn.ui.components.UICanvas

interface UICanvasShape: UICanvas {
    val paint: Paint

    fun prepareAnimation(
        start: Any,
        end: Any
    )

    fun tickAnimation(
        t: Float
    )

}
package good.damn.ui.components

import android.graphics.Canvas

interface UICanvas {
    fun draw(
        canvas: Canvas
    )

    fun layout(
        width: Float,
        height: Float
    )
}
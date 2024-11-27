package good.damn.ui.components.shapes

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import good.damn.ui.components.UICanvas

class UICanvasRectRound(
    private val rect: RectF,
    var radius: Float,
    val rotation: Float = 0.0f
): UICanvasShape {

    override val paint = Paint()

    override fun draw(
        canvas: Canvas
    ) = canvas.run {

        save()
        rotate(
            rotation,
            rect.left + rect.width() * 0.5f,
            rect.top + rect.height() * 0.5f
        )
        drawRoundRect(
            rect,
            radius,
            radius,
            paint
        )

        restore()
    }

}
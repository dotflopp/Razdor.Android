package good.damn.ui.components.shapes

import android.graphics.Canvas
import android.graphics.Paint
import good.damn.ui.components.UICanvas

class UICanvasCircle(
    var x: Float,
    var y: Float,
    var radius: Float
): UICanvasShape {

    override val paint = Paint()

    override fun draw(
        canvas: Canvas
    ) {
        canvas.drawCircle(
            x,
            y,
            radius,
            paint
        )
    }

}
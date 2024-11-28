package good.damn.ui.components.shapes

import android.graphics.Canvas
import android.graphics.Paint
import good.damn.ui.components.UICanvas
import good.damn.ui.components.shapes.animation.data.UICanvasShapeAnimationCircle
import good.damn.ui.extensions.primitives.interpolate

class UICanvasCircle(
    var x: Float,
    var y: Float,
    var radius: Float
): UICanvasShape {

    private var mStart: UICanvasShapeAnimationCircle? = null
    private var mEnd: UICanvasShapeAnimationCircle? = null

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

    override fun prepareAnimation(
        start: Any,
        end: Any
    ) {
        mStart = start as? UICanvasShapeAnimationCircle
        mEnd = end as? UICanvasShapeAnimationCircle
    }

    override fun tickAnimation(
        t: Float
    ) {
        mStart?.let { start ->
            mEnd?.let { end ->
                x = start.x.interpolate(
                    end.x,
                    t
                )

                y = start.y.interpolate(
                    end.y,
                    t
                )

                radius = start.radius.interpolate(
                    end.radius,
                    t
                )
            }
        }
    }

}
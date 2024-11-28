package good.damn.ui.components.shapes

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import good.damn.ui.components.UICanvas
import good.damn.ui.components.shapes.animation.data.UICanvasShapeAnimationCircle
import good.damn.ui.components.shapes.animation.data.UICanvasShapeAnimationRectRound
import good.damn.ui.extensions.primitives.interpolate

class UICanvasRectRound(
    private val rect: RectF,
    var radius: Float,
    var rotation: Float = 0.0f
): UICanvasShape {

    private var mStart: UICanvasShapeAnimationRectRound? = null
    private var mEnd: UICanvasShapeAnimationRectRound? = null

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

    override fun prepareAnimation(
        start: Any,
        end: Any
    ) {
        mStart = start as? UICanvasShapeAnimationRectRound
        mEnd = end as? UICanvasShapeAnimationRectRound
    }

    override fun tickAnimation(
        t: Float
    ) {
        mStart?.let { start ->
            mEnd?.let { end ->
                rect.top = start.top.interpolate(
                    end.top,
                    t
                )

                rect.left = start.left.interpolate(
                    end.left,
                    t
                )

                rect.bottom = start.bottom.interpolate(
                    end.bottom,
                    t
                )

                rect.right = start.right.interpolate(
                    end.right,
                    t
                )

                radius = start.radius.interpolate(
                    end.radius,
                    t
                )

                rotation = start.rotation.interpolate(
                    end.rotation,
                    t
                )
            }
        }
    }

}
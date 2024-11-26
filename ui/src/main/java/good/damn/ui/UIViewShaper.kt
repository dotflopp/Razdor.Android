package good.damn.ui

import android.content.Context
import android.graphics.Canvas
import android.view.View
import good.damn.ui.components.UICanvas
import good.damn.ui.components.shapes.UICanvasShape
import good.damn.ui.interfaces.UIThemable
import good.damn.ui.theme.UITheme

class UIViewShaper(
    context: Context
): View(
    context
), UIThemable {

    init {
        background = null
    }

    var shapes: Array<UICanvasShape>? = null

    override fun applyTheme(
        theme: UITheme
    ) {
        shapes?.forEach {
            it.paint.color = theme.colorShape
        }
    }

    override fun onDraw(
        canvas: Canvas
    )  {
        shapes?.forEach {
            it.draw(
                canvas
            )
        }
    }

}
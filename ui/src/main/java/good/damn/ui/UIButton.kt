package good.damn.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import good.damn.ui.components.UICanvasText
import good.damn.ui.theme.UITheme

open class UIButton(
    context: Context
): UIView(
    context
) {

    @setparam:ColorInt
    @get:ColorInt
    var textColor: Int
        get() = mCanvasText.color
        set(v) {
            mCanvasText.color = v
        }

    var typeface: Typeface
        get() = mCanvasText.typeface
        set(v) {
            mCanvasText.typeface = v
        }

    var textSizeFactor = 0.2f

    var text: String?
        get() = mCanvasText.text
        set(v) {
            mCanvasText.text = v
        }


    private val mCanvasText = UICanvasText()

    fun setTextId(
        @StringRes id: Int
    ) {
        mCanvasText.text = context.getString(id)
    }

    override fun onLayout(
        changed: Boolean,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ) {
        super.onLayout(
            changed, left,
            top, right, bottom
        )

        mCanvasText.textSize = height * textSizeFactor

        mCanvasText.layout(
            mRect.right,
            mRect.bottom
        )
    }

    override fun onDraw(
        canvas: Canvas
    ) = canvas.run {
        super.onDraw(
            canvas
        )

        mCanvasText.draw(
            canvas
        )
    }

    override fun applyTheme(
        theme: UITheme
    ) {
        mPaintBackground.color = theme.colorBackgroundButton
        mCanvasText.color = theme.colorTextButton
    }
}
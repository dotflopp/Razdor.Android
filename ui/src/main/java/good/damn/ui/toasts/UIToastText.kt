package good.damn.ui.toasts

import android.content.Context
import android.graphics.Canvas
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import good.damn.ui.components.UICanvasText
import good.damn.ui.theme.UITheme

class UIToastText(
    context: Context
): UIToast(
    context
) {
    var text: CharSequence?
        get() = mTextView.text
        set(v) {
            mTextView.text = v
        }

    private val mTextView = TextView(
        context
    ).apply {
        background = null
        addView(this)
    }

    override fun applyTheme(
        theme: UITheme
    ) {
        mTextView.setTextColor(
            theme.colorText
        )
    }
}
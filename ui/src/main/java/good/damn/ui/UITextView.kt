package good.damn.ui

import android.content.Context
import androidx.appcompat.widget.AppCompatTextView
import good.damn.ui.interfaces.UIThemable
import good.damn.ui.theme.UITheme

class UITextView(
    context: Context
): AppCompatTextView(
    context
), UIThemable {

    init {
        background = null
    }

    override fun applyTheme(
        theme: UITheme
    ) {
        setTextColor(
            theme.colorText
        )
    }
}
package good.damn.ui.layouts

import android.content.Context
import android.widget.FrameLayout
import good.damn.ui.interfaces.UIThemable
import good.damn.ui.theme.UITheme

class UIFrameLayout(
    context: Context
): FrameLayout(
    context
), UIThemable {

    init {
        clipChildren = false
    }

    override fun applyTheme(
        theme: UITheme
    ) {
        setBackgroundColor(
            theme.colorBackground
        )
    }
}
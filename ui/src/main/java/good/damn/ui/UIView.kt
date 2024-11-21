package good.damn.ui

import android.content.Context
import android.view.View
import good.damn.ui.theme.UITheme

abstract class UIView(
    context: Context
): View(
    context
) {
    abstract fun applyTheme(
        theme: UITheme
    )
}
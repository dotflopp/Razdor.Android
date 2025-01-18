package good.damn.ui.interfaces

import good.damn.ui.theme.UITheme

interface UIThemable {
    fun applyTheme(
        theme: UITheme
    )
}
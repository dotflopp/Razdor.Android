package good.damn.ui.buttons

import android.content.Context
import good.damn.ui.theme.UITheme

class UIButtonSemi(
    context: Context
): UIButton(
    context
) {
    override fun applyTheme(
        theme: UITheme
    ) {
        mCanvasText.color = theme.colorTextButtonSemi
        mCanvasText2.color = theme.colorTextButtonSemi
        mPaintBackground.color = theme.colorBackgroundButtonSemi
    }
}
package good.damn.ui.extensions

import androidx.appcompat.widget.AppCompatEditText
import good.damn.ui.theme.UITheme

inline fun AppCompatEditText.applyTheme(
    theme: UITheme
) {
    setHintTextColor(
        theme.colorHint
    )

    setTextColor(
        theme.colorText
    )
}
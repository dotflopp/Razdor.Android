package good.damn.ui.extensions

import android.util.TypedValue
import android.widget.TextView
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat

inline fun TextView.setTextSizePx(
    s: Float
) = setTextSize(
    TypedValue.COMPLEX_UNIT_PX,
    s
)

inline fun TextView.setTypefaceId(
    @FontRes id: Int
) {
    typeface = ResourcesCompat.getFont(
        context,
        id
    )
}
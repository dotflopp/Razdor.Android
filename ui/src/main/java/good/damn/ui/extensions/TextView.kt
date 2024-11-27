package good.damn.ui.extensions

import android.util.TypedValue
import android.view.View
import android.view.View.MeasureSpec
import android.widget.TextClock
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

inline fun TextView.makeMeasuredHeight(
    width: Int
): Int {
    measure(
        MeasureSpec.makeMeasureSpec(
            width,
            MeasureSpec.EXACTLY
        ),
        MeasureSpec.makeMeasureSpec(
            0,
            MeasureSpec.UNSPECIFIED
        )
    )

    return measuredHeight
}
package good.damn.ui.extensions

import android.content.Context
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat

inline fun Context.getFont(
    @FontRes id: Int
) = ResourcesCompat.getFont(
    this,
    id
)
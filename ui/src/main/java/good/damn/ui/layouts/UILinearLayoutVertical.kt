package good.damn.ui.layouts

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.allViews
import androidx.core.view.children
import good.damn.ui.extensions.makeMeasuredHeight
import good.damn.ui.interfaces.UIThemable
import good.damn.ui.theme.UITheme
import kotlin.math.log

open class UILinearLayoutVertical(
    context: Context
): FrameLayout(
    context
), UIThemable {

    companion object {
        private val TAG = UILinearLayoutVertical::class.simpleName
    }

    init {
        clipChildren = false
    }

    private var mLastY = 0

    override fun applyTheme(
        theme: UITheme
    ) {
        setBackgroundColor(
            theme.colorBackground
        )
    }

    override fun onLayout(
        changed: Boolean,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ) {
        super.onLayout(
            changed,
            left, top,
            right, bottom
        )
    }

    override fun addView(
        child: View
    ) {
        val params = child.layoutParams
            as? FrameLayout.LayoutParams
            ?: return

        params.topMargin += mLastY

        super.addView(
            child
        )

        mLastY = params.topMargin + if (
            params.height >= 0
        ) params.height else (
            child as? TextView
        )?.makeMeasuredHeight(
            params.width
        ) ?: 0

    }

}
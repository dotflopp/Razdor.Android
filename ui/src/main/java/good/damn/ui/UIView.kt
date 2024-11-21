package good.damn.ui

import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.view.View
import good.damn.ui.extensions.isOutsideView
import good.damn.ui.theme.UITheme

abstract class UIView(
    context: Context
): View(
    context
) {
    
    companion object {
        private val TAG = UIView::class.simpleName
    }
    
    private var mOnClick: OnClickListener? = null

    final override fun setOnClickListener(
        l: OnClickListener?
    ) {
        mOnClick = l
    }

    override fun onTouchEvent(
        event: MotionEvent?
    ): Boolean {

        if (event == null) {
            return false
        }

        when (event.action) {
            MotionEvent.ACTION_UP -> {
                if (isOutsideView(
                    event.x,
                    event.y
                )) {
                    return true
                }

                mOnClick?.onClick(
                    this
                )
            }
        }

        return true
    }


    abstract fun applyTheme(
        theme: UITheme
    )
}
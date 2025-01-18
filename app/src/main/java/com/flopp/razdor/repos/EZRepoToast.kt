package com.flopp.razdor.repos

import android.content.Context
import android.view.Gravity
import android.widget.FrameLayout
import com.flopp.razdor.EZApp
import com.flopp.razdor.extensions.view.boundsFrame
import good.damn.ui.toasts.UIToastText

class EZRepoToast {

    var container: FrameLayout? = null

    private var mCurrentToast: FrameLayout? = null

    fun toast(
        toastView: UIToastText
    ) {
        mCurrentToast = toastView
        container?.addView(
            toastView
        )
    }

    fun toast(
        context: Context,
        msg: String
    ) {
        if (mCurrentToast != null) {
            return
        }

        UIToastText(
            context
        ).apply {

            setBackgroundColor(
                EZApp.theme.colorBackground
            )

            applyTheme(
                EZApp.theme
            )

            text = msg

            boundsFrame(
                gravity = Gravity.CENTER_HORIZONTAL or
                    Gravity.BOTTOM,
                width = EZApp.width * 0.5f,
                height = EZApp.height * 0.05f,
                bottom = EZApp.height * 0.2f
            )

            mCurrentToast = this

            container?.addView(
                this
            )

            show()
        }
    }

}
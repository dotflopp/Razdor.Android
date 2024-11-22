package com.flopp.razdor.services

import android.content.Context
import android.widget.FrameLayout
import good.damn.ui.toasts.UIToastText

class EZServiceToast {

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

            text = msg

            container?.addView(
                this
            )
        }
    }

}
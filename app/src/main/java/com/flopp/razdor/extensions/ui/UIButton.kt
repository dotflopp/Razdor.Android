package com.flopp.razdor.extensions.ui

import android.view.View
import com.flopp.razdor.EZApp
import com.flopp.razdor.extensions.mainActivity
import com.flopp.razdor.extensions.services.vibrateCompat
import good.damn.ui.UIButton

inline fun UIButton.setupVibration() {
    onClickDisabled = View.OnClickListener {
        EZApp.services?.vibrator?.apply {
            vibrateCompat(35)
        }
        startShakeAnimation()
    }
}
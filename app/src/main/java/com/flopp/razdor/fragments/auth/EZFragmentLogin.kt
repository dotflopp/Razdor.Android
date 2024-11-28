package com.flopp.razdor.fragments.auth

import android.content.Context
import com.flopp.razdor.EZApp
import com.flopp.razdor.fragments.navigation.EZFragmentNavigation
import com.flopp.razdor.views.fragments.EZViewLogin

class EZFragmentLogin
: EZPageableFragment() {

    override fun onCreateView(
        context: Context
    ) = EZViewLogin(
        context
    ).apply {

        background = null

        setPadding(
            0,
            EZApp.insetTop.toInt(),
            0,
            0
        )

        applyTheme(
            EZApp.theme
        )
    }

}
package com.flopp.razdor.fragments

import android.content.Context
import com.flopp.razdor.fragments.navigation.EZFragmentNavigation
import com.flopp.razdor.views.fragments.EZViewLogin

class EZFragmentLogin
: EZFragmentNavigation() {

    override fun onCreateView(
        context: Context
    ) = EZViewLogin(
        context
    )

}
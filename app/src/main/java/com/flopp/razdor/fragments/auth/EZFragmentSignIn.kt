package com.flopp.razdor.fragments.auth

import android.content.Context
import com.flopp.razdor.EZApp
import com.flopp.razdor.fragments.auth.interfaces.EZListenerOnSignInSuccess
import com.flopp.razdor.fragments.navigation.EZFragmentNavigation
import good.damn.ui.layouts.UILinearLayoutVertical

class EZFragmentSignIn
: EZPageableFragment() {

    var onSignInSuccess: EZListenerOnSignInSuccess? = null

    override fun onCreateView(
        context: Context
    ) = UILinearLayoutVertical(
        context
    ).apply {
        background = null

    }

}
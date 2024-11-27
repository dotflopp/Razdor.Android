package com.flopp.razdor.fragments

import android.content.Context
import android.view.View
import com.flopp.razdor.EZApp
import com.flopp.razdor.fragments.navigation.EZFragmentNavigation
import good.damn.ui.layouts.UILinearLayoutVertical

class EZFragmentSignIn
: EZFragmentNavigation() {

    override fun onCreateView(
        context: Context
    ) = UILinearLayoutVertical(
        context
    ).apply {

        applyTheme(
            EZApp.theme
        )
    }

}
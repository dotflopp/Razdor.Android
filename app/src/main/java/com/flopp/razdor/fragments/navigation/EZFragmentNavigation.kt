package com.flopp.razdor.fragments.navigation

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.flopp.razdor.activities.EZActivityMain
import com.flopp.razdor.extensions.mainActivity
import com.flopp.razdor.navigation.EZNavigationFragment
import good.damn.ui.interfaces.UIThemable
import good.damn.ui.theme.UITheme

abstract class EZFragmentNavigation
: Fragment(),
UIThemable {

    var navigation: EZNavigationFragment<
        EZFragmentNavigation
    >? = null

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val context = context
            ?: return null

        return onCreateView(
            context
        )
    }

    open fun backPressed() {
        navigation?.pop()
    }

    override fun applyTheme(
        theme: UITheme
    ) = Unit

    protected abstract fun onCreateView(
        context: Context
    ): View

}
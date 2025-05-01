package com.example.zov_android.ui.fragments.navigation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

abstract class NavigableFragment: Fragment() {

    var navigation: NavigationFragment<NavigableFragment>? = null

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val context = context
            ?: return null

        return onCreateView(context)
    }

    open fun backPressed() {
        navigation?.pop()
    }



    /*override fun applyTheme(
        theme: UITheme
    ) = Unit*/

    abstract fun onCreateView(
        context: Context
    ): View

}
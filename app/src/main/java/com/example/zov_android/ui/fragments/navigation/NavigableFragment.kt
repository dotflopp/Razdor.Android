package com.example.zov_android.ui.fragments.navigation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import javax.inject.Inject

abstract class NavigableFragment: Fragment() {
    lateinit var navigation: NavigationFragment
    lateinit var navigationInside: NavigationInsideFragment
    open override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val context = context
            ?: return null

        return onCreateView(context)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    abstract fun onCreateView(
        context: Context
    ): View
}
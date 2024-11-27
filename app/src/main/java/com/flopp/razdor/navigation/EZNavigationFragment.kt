package com.flopp.razdor.navigation

import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class EZNavigationFragment<FRAGMENT: Fragment>(
    private val fragmentManager: FragmentManager,
    private val container: FrameLayout
) {

    val size: Int
        get() = fragmentManager
            .fragments
            .size

    fun push(
        fragment: FRAGMENT
    ) = fragmentManager.beginTransaction()
        .add(
            container.id,
            fragment
        ).commit()


    fun pop() = fragmentManager
        .beginTransaction()
        .remove(
            fragmentManager.fragments.removeLast()
        ).commit()
}
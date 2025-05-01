package com.example.zov_android.ui.fragments.navigation

import android.widget.FrameLayout
import androidx.fragment.app.FragmentManager

class NavigationFragment<FRAGMENT: NavigableFragment>(
    private val fragmentManager: FragmentManager,
    private val container: FrameLayout
) {

    val size: Int
        get() = fragmentManager.fragments.size

    fun last() = fragmentManager.fragments.lastOrNull() as? NavigableFragment

    fun push(
        fragment: FRAGMENT,
        tag: String? = null
    ) {
        fragmentManager.beginTransaction().apply{
            add(container.id, fragment, tag)
            addToBackStack(null)
        }.commit()
    }


    fun pop() = fragmentManager
        .beginTransaction()
        .remove(fragmentManager.fragments.last())
        .commit()
}
package com.example.zov_android.navigation

import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import java.util.LinkedList

class EZNavigationFragment<FRAGMENT: Fragment>(
    private val fragmentManager: FragmentManager,
    private val container: FrameLayout
) {

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
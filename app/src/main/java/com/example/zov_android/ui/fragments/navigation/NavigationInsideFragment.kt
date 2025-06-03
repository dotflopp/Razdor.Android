package com.example.zov_android.ui.fragments.navigation

import android.widget.FrameLayout
import androidx.fragment.app.FragmentManager
import com.example.zov_android.R

class NavigationInsideFragment(
    private val childFragmentManager: FragmentManager,
    private val container: FrameLayout
) {

    fun push(
        fragment: NavigableFragment,
        tag: String? = null
    ) {
        fragment.navigationInside = this
        childFragmentManager.beginTransaction().apply{
            replace(container.id, fragment, tag)
            //addToBackStack(tag)
        }.commit()
    }

    fun pushUp(
        fragment: NavigableFragment,
        tag: String? = null
    ) {
        fragment.navigationInside = this
        childFragmentManager.beginTransaction().apply{
            setCustomAnimations(
                R.anim.slide_in_up,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out_down
            )
            add(container.id, fragment, tag)
            addToBackStack(tag)
        }.commit()
    }

}
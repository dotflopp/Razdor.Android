package com.example.zov_android.ui.fragments.navigation

import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.example.zov_android.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent


open class NavigationFragment(
    private val fragmentManager: FragmentManager,
    private val container: FrameLayout
) {

    val size: Int
        get() = fragmentManager.fragments.size

    fun last() = fragmentManager.fragments.lastOrNull() as? NavigableFragment

    fun push(
        fragment: NavigableFragment,
        tag: String? = null
    ) {
        fragment.navigation = this
        fragmentManager.beginTransaction().apply{
            replace(container.id, fragment, tag)
            addToBackStack(null)
        }.commit()
    }


    fun pop() = fragmentManager
        .beginTransaction()
        .remove(fragmentManager.fragments.last())
        .commit()
}
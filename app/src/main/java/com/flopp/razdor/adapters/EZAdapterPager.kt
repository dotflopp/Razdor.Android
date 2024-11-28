package com.flopp.razdor.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class EZAdapterPager<T: Fragment>(
    val fragments: Array<T>,
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
): FragmentStateAdapter(
    fragmentManager,
    lifecycle
) {

    override fun getItemCount()
        = fragments.size

    override fun createFragment(
        position: Int
    ) = fragments[position]


}
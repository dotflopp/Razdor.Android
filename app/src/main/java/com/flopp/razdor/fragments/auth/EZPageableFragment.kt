package com.flopp.razdor.fragments.auth

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.flopp.razdor.pagers.EZViewPagerShaper

abstract class EZPageableFragment
: Fragment() {



    override fun onCreateView(
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

    abstract fun onCreateView(
        context: Context
    ): View?

}
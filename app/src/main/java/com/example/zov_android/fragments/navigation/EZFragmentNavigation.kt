package com.example.zov_android.fragments.navigation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

abstract class EZFragmentNavigation
: Fragment() {

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


    protected abstract fun onCreateView(
        context: Context
    ): View

}
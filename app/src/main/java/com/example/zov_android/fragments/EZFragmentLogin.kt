package com.example.zov_android.fragments

import android.content.Context
import com.example.zov_android.fragments.navigation.EZFragmentNavigation
import com.example.zov_android.views.fragments.EZViewLogin

class EZFragmentLogin
: EZFragmentNavigation() {

    override fun onCreateView(
        context: Context
    ) = EZViewLogin(
        context
    )

}
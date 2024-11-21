package com.example.zov_android.fragments

import android.content.Context
import android.view.View
import com.example.zov_android.fragments.navigation.EZFragmentNavigation
import com.example.zov_android.views.EZViewLogin

class EZFragmentLogin
: EZFragmentNavigation() {

    override fun onCreateView(
        context: Context
    ) = EZViewLogin(
        context
    )

}
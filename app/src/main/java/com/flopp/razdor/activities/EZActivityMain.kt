package com.flopp.razdor.activities

import android.os.Bundle
import android.widget.FrameLayout
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.flopp.razdor.fragments.EZFragmentLogin
import com.flopp.razdor.navigation.EZNavigationFragment
import com.flopp.razdor.services.EZServiceToast

class EZActivityMain
: AppCompatActivity() {

    private var mFragmentNavigation: EZNavigationFragment<Fragment>? = null

    private val mServiceToast = EZServiceToast()

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(
            savedInstanceState
        )

        val context = this

        window.setBackgroundDrawable(null)

        FrameLayout(
            context
        ).let { root ->

            mServiceToast.container = root

            FrameLayout(
                context
            ).apply {
                id = ViewCompat.generateViewId()

                root.addView(
                    this
                )

                mFragmentNavigation = EZNavigationFragment(
                    supportFragmentManager,
                    this
                )
            }

            setContentView(
                root
            )
        }

        mFragmentNavigation?.push(
            EZFragmentLogin()
        )
    }

    fun toast(
        @StringRes id: Int
    ) {
        mServiceToast.toast(
            this,
            getString(id)
        )
    }

    fun toast(
        msg: String
    ) {
        mServiceToast.toast(
            this,
            msg
        )
    }

}
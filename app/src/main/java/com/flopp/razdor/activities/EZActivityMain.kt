package com.flopp.razdor.activities

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.flopp.razdor.fragments.EZFragmentLogin
import com.flopp.razdor.navigation.EZNavigationFragment

class EZActivityMain
: AppCompatActivity() {

    private var mFragmentNavigation: EZNavigationFragment<Fragment>? = null

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
        ).apply {
            id = ViewCompat.generateViewId()

            setContentView(
                this
            )
            mFragmentNavigation = EZNavigationFragment(
                supportFragmentManager,
                this
            )
        }

        mFragmentNavigation?.push(
            EZFragmentLogin()
        )
    }

}
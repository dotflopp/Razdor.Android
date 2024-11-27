package com.flopp.razdor.activities

import android.os.Bundle
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.flopp.razdor.EZApp
import com.flopp.razdor.activities.callbacks.EZCallbackBackPressedNavigation
import com.flopp.razdor.fragments.EZFragmentIntro
import com.flopp.razdor.fragments.navigation.EZFragmentNavigation
import com.flopp.razdor.navigation.EZNavigationFragment
import com.flopp.razdor.services.EZServiceToast

class EZActivityMain
: AppCompatActivity() {

    companion object {
        private val TAG = EZActivityMain::class.simpleName
    }

    var fragmentNavigation: EZNavigationFragment<
        EZFragmentNavigation
    >? = null

    private val mServiceToast = EZServiceToast()

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(
            savedInstanceState
        )

        val context = this

        window.apply {
            setBackgroundDrawable(null)
            setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }

        ViewCompat.setOnApplyWindowInsetsListener(
            window.decorView
        ) { v, insets ->
            
            EZApp.insetTop = insets.stableInsetTop.toFloat()
            EZApp.insetBottom = insets.stableInsetBottom.toFloat()

            initView()

            ViewCompat.setOnApplyWindowInsetsListener(
                v, null
            )

            WindowInsetsCompat.CONSUMED
        }

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

    private inline fun initView() {
        val context = this

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

                fragmentNavigation = EZNavigationFragment(
                    supportFragmentManager,
                    this
                )
            }

            setContentView(
                root
            )
        }

        fragmentNavigation?.let {
            onBackPressedDispatcher.addCallback(
                this,
                EZCallbackBackPressedNavigation(
                    it,
                    this
                )
            )

            it.push(
                EZFragmentIntro()
            )
        }


    }

}
package com.flopp.razdor.fragments.auth

import android.content.Context
import android.graphics.RectF
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.flopp.razdor.EZApp
import com.flopp.razdor.adapters.EZAdapterPager
import com.flopp.razdor.fragments.navigation.EZFragmentNavigation
import com.flopp.razdor.navigation.EZNavigationFragment
import good.damn.ui.UIViewShaper
import good.damn.ui.components.shapes.UICanvasCircle
import good.damn.ui.components.shapes.UICanvasRectRound
import good.damn.ui.layouts.UIFrameLayout

class EZFragmentAuth
: EZFragmentNavigation() {

    private var mViewPager: ViewPager2? = null

    override fun onCreateView(
        context: Context
    ) = UIFrameLayout(
        context
    ).let { root ->
        root.applyTheme(
            EZApp.theme
        )

        UIViewShaper(
            context
        ).apply {
            val h = EZApp.height
            val w = EZApp.width

            shapes = arrayOf(
                UICanvasCircle(
                    x = 0.0f,
                    y = h * 0.25f,
                    radius = w * 0.2f
                ),
                UICanvasCircle(
                    x = w * 0.3f,
                    y = h * 0.95f,
                    radius = w * 0.4f
                ),
                UICanvasRectRound(
                    RectF(
                        w * 0.1f,
                        h * 0.42f,
                        w * 0.8f,
                        h * 0.59f
                    ),
                    radius = w * 0.12f,
                    rotation = -25f
                ),
                UICanvasRectRound(
                    RectF(
                        w * 0.9f,
                        h * 0.1f,
                        w * 1.05f,
                        h * 0.49f
                    ),
                    radius = w * 0.12f,
                    rotation = -25f
                )
            )

            applyTheme(
                EZApp.theme
            )

            root.addView(
                this
            )
        }

        mViewPager = ViewPager2(
            context
        ).apply {
            background = null
            id = ViewCompat.generateViewId()

            isUserInputEnabled = false

            val fragments = arrayOf(
                EZFragmentIntro(),
                EZFragmentSignIn(),
                EZFragmentLogin()
            )

            fragments.forEach {
                it.pager = this
            }

            adapter = EZAdapterPager(
                fragments,
                childFragmentManager,
                lifecycle
            )

            root.addView(
                this
            )
        }

        return@let root
    }

    override fun backPressed() {
        Log.d("EZFragmentAuth", "backPressed: ")
        mViewPager?.apply {
            if (currentItem == 0) {
                super.backPressed()
                return@apply
            }
            currentItem = 0
        }

    }

}
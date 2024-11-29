package com.flopp.razdor.fragments.auth

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import com.flopp.razdor.EZApp
import com.flopp.razdor.R
import com.flopp.razdor.extensions.view.boundsFrame
import good.damn.ui.animation.UIAnimationGroup
import good.damn.ui.animation.UIAnimationScale
import good.damn.ui.buttons.UIButton
import good.damn.ui.buttons.UIButtonSemi
import good.damn.ui.extensions.getFont
import good.damn.ui.layouts.UIFrameLayout

class EZFragmentIntro
: EZPageableFragment() {

    var onClickLogin: View.OnClickListener? = null
    var onClickSignIn: View.OnClickListener? = null

    override fun onCreateView(
        context: Context
    ) = UIFrameLayout(
        context
    ).apply {

        background = null

        setPadding(
            0,
            EZApp.insetTop.toInt(),
            0,
            EZApp.insetBottom.toInt()
        )

        val btnWidth = EZApp.width * 0.85f
        val btnHeight = EZApp.height * 0.08f
        val marginBottom = EZApp.height * 0.05f

        UIButtonSemi(
            context
        ).apply {
            setOnClickListener(
                onClickLogin
            )

            animationGroupTouchDown = UIAnimationGroup(
                arrayOf(
                    UIAnimationScale(
                        1.0f,
                        0.9f
                    )
                ),
                OvershootInterpolator(),
                150L
            )

            animationGroupTouchUp = UIAnimationGroup(
                arrayOf(
                    UIAnimationScale(
                        0.9f,
                        1.0f
                    )
                ),
                OvershootInterpolator(),
                150L
            )

            text = context.getString(
                R.string.login
            )

            cornerRadiusFactor = 0.2f

            textSizeFactor = 0.3f

            typeface = context.getFont(
                R.font.open_sans_bold
            )

            applyTheme(
                EZApp.theme
            )

            boundsFrame(
                gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM,
                width = btnWidth,
                height = btnHeight,
                bottom = marginBottom
            )

            addView(
                this
            )

        }

        UIButton(
            context
        ).apply {

            setOnClickListener(
                onClickSignIn
            )

            text = context.getString(
                R.string.signin
            )

            cornerRadiusFactor = 0.2f

            textSizeFactor = 0.3f

            typeface = context.getFont(
                R.font.open_sans_bold
            )

            applyTheme(
                EZApp.theme
            )

            boundsFrame(
                gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM,
                width = btnWidth,
                height = btnHeight,
                bottom = marginBottom * 1.5f + btnHeight
            )

            addView(
                this
            )
        }

    }

}
package com.flopp.razdor.fragments

import android.content.Context
import android.view.Gravity
import android.view.View
import com.flopp.razdor.EZApp
import com.flopp.razdor.R
import com.flopp.razdor.extensions.view.boundsFrame
import com.flopp.razdor.fragments.navigation.EZFragmentNavigation
import good.damn.ui.UIButton
import good.damn.ui.extensions.getFont
import good.damn.ui.interfaces.UIThemable
import good.damn.ui.layouts.UIFrameLayout
import good.damn.ui.layouts.UILinearLayoutVertical
import good.damn.ui.theme.UITheme

class EZFragmentIntro
: EZFragmentNavigation() {

    override fun onCreateView(
        context: Context
    ) = UIFrameLayout(
        context
    ).apply {

        setPadding(
            0,
            EZApp.insetTop.toInt(),
            0,
            EZApp.insetBottom.toInt()
        )

        applyTheme(
            EZApp.theme
        )

        val btnWidth = EZApp.width * 0.85f
        val btnHeight = EZApp.height * 0.08f

        UIButton(
            context
        ).apply {

            setOnClickListener {
                onClickBtnLogin(this)
            }

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
                height = btnHeight
            )

            addView(
                this
            )
        }

    }


    private inline fun onClickBtnLogin(
        btn: UIButton
    ) {
        navigation?.push(
            EZFragmentLogin()
        )
    }

}
package com.flopp.razdor.fragments.auth

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import com.flopp.razdor.EZApp
import com.flopp.razdor.R
import com.flopp.razdor.extensions.view.boundsFrame
import good.damn.editor.importer.VEModelImport
import good.damn.editor.importer.VEViewAVS
import good.damn.sav.misc.Size
import good.damn.ui.UITextView
import good.damn.ui.UITextViewSemi
import good.damn.ui.animation.UIAnimationGroup
import good.damn.ui.animation.UIAnimationScale
import good.damn.ui.buttons.UIButton
import good.damn.ui.buttons.UIButtonSemi
import good.damn.ui.extensions.getFont
import good.damn.ui.extensions.makeMeasuredHeight
import good.damn.ui.extensions.setTextSizePx
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

        val btnSignInMargin = marginBottom * 1.5f + btnHeight
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
                bottom = btnSignInMargin
            )

            addView(
                this
            )
        }

        var textViewQuoteMargin: Float
        UITextViewSemi(
            context
        ).apply {

            setText(
                R.string.introQuote
            )

            setTextSizePx(
                btnHeight * 0.25f
            )

            gravity = Gravity.CENTER_HORIZONTAL

            typeface = context.getFont(
                R.font.open_sans_bold
            )

            alpha = 0.6f

            applyTheme(
                EZApp.theme
            )

            val w = btnWidth * 0.8f
            val h = makeMeasuredHeight(
                w.toInt()
            ).toFloat()

            textViewQuoteMargin = btnSignInMargin +
                btnHeight +
                marginBottom * 1.2f

            boundsFrame(
                gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM,
                width = w,
                height = h,
                bottom = textViewQuoteMargin
            )

            textViewQuoteMargin += h

            addView(
                this
            )
        }

        UITextView(
            context
        ).apply {

            setText(
                R.string.porazdorim
            )

            setTextSizePx(
                btnHeight * 0.6f
            )

            gravity = Gravity.CENTER_HORIZONTAL

            typeface = context.getFont(
                R.font.open_sans_extra_bold
            )


            applyTheme(
                EZApp.theme
            )

            boundsFrame(
                gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM,
                width = btnWidth,
                bottom = textViewQuoteMargin + marginBottom * 0.1f
            )

            addView(
                this
            )
        }

    }

}
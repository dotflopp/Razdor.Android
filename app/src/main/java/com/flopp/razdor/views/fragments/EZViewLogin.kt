package com.flopp.razdor.views.fragments

import android.content.Context
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import com.flopp.razdor.EZApp
import com.flopp.razdor.R
import com.flopp.razdor.extensions.view.boundsLinear
import good.damn.ui.UIButton
import good.damn.ui.UITextView
import good.damn.ui.animation.UIAnimationCornerRadius
import good.damn.ui.animation.UIAnimationGroup
import good.damn.ui.animation.UIAnimationScale
import good.damn.ui.extensions.getFont
import good.damn.ui.extensions.setTextSizePx
import good.damn.ui.extensions.setTypefaceId
import good.damn.ui.textfield.UITextField

class EZViewLogin(
    context: Context
): LinearLayout(
    context
) {

    init {
        setBackgroundColor(
            EZApp.theme.colorBackground
        )

        orientation = VERTICAL

        val margin = EZApp.height * 0.02f
        val fieldHeight = EZApp.height * 0.09f
        val fieldWidth = EZApp.width * 0.85f
        val strokeWidthh = fieldHeight * 0.04f

        val typefaceFieldHint = context.getFont(
            R.font.open_sans_bold
        )

        UITextView(
            context
        ).apply {

            setText(
                R.string.welcome_back
            )

            setTypefaceId(
                R.font.open_sans_extra_bold
            )

            setTextSizePx(
                fieldHeight * 0.4f
            )

            applyTheme(
                EZApp.theme
            )

            boundsLinear(
                gravity = Gravity.CENTER_HORIZONTAL
            )

            addView(
                this
            )
        }

        val editTextUsername = UITextField(
            context
        ).apply {

            subhint = context.getString(
                R.string.warningUsername
            )

            hint = context.getString(
                R.string.username
            )

            setTypefaceId(
                R.font.open_sans_regular
            )

            typefaceSubhint = typefaceFieldHint
            typefaceHint = typefaceFieldHint

            cornerRadiusFactor = 0.3f

            strokeWidth = strokeWidthh

            applyTheme(
                EZApp.theme
            )

            boundsLinear(
                gravity = Gravity.CENTER_HORIZONTAL,
                width = fieldWidth,
                height = fieldHeight,
                top = margin
            )

            this@EZViewLogin.addView(
                this
            )
        }

        val editTextPassword = UITextField(
            context
        ).apply {

            hint = context.getString(
                R.string.password
            )

            subhint = context.getString(
                R.string.warningPassword
            )

            setTypefaceId(
                R.font.open_sans_regular
            )

            typefaceSubhint = typefaceFieldHint
            typefaceHint = typefaceFieldHint

            cornerRadiusFactor = 0.3f
            strokeWidth = strokeWidthh

            applyTheme(
                EZApp.theme
            )

            boundsLinear(
                gravity = Gravity.CENTER_HORIZONTAL,
                width = fieldWidth,
                height = fieldHeight,
                top = margin
            )

            addView(
                this
            )
        }

        UIButton(
            context
        ).apply {

            text = context.getString(
                R.string.login
            )

            typeface = context.getFont(
                R.font.open_sans_bold
            )

            setOnClickListener {
                onClickBtnLogin(
                    this,
                    editTextUsername,
                    editTextPassword
                )
            }

            animationGroupTouchDown = UIAnimationGroup(
                arrayOf(
                    UIAnimationScale(
                        1.0f,
                        0.85f
                    ),
                    UIAnimationCornerRadius(
                        0.25f,
                        0.5f
                    )
                ),
                OvershootInterpolator(),
                200
            )

            animationGroupTouchUp = UIAnimationGroup(
                arrayOf(
                    UIAnimationScale(
                        0.85f,
                        1.0f
                    ),
                    UIAnimationCornerRadius(
                        0.5f,
                        0.25f
                    )
                ),
                AccelerateDecelerateInterpolator(),
                125
            )

            textSizeFactor = 0.32f
            cornerRadiusFactor = 0.35f

            applyTheme(
                EZApp.theme
            )

            boundsLinear(
                gravity = Gravity.CENTER_HORIZONTAL,
                width = fieldWidth,
                height = EZApp.height * 0.07f,
                top = margin
            )

            addView(
                this
            )
        }

    }


    private inline fun onClickBtnLogin(
        btn: UIButton,
        username: UITextField,
        password: UITextField
    ) {
        var hasError = false

        if (username.text?.toString()?.isBlank() != false) {
            username.error(
                EZApp.theme
            )
            hasError = true
        }

        if (password.text?.toString()?.isBlank() != false) {
            password.error(
                EZApp.theme
            )
            hasError = true
        }

        if (hasError) {
            return
        }


        Handler(
            Looper.getMainLooper()
        ).apply {

            btn.changeTextAnimated(
                "Waiting..."
            )

            postDelayed({
                btn.changeTextAnimated(
                    "Ready :)"
                )
            }, 2500)

        }

    }
}
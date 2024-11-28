package com.flopp.razdor.views.fragments

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.method.PasswordTransformationMethod
import android.view.Gravity
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import com.flopp.razdor.EZApp
import com.flopp.razdor.R
import com.flopp.razdor.extensions.ui.setupVibration
import com.flopp.razdor.extensions.view.boundsFrame
import com.flopp.razdor.patterns.EZPattern
import good.damn.ui.buttons.UIButton
import good.damn.ui.UITextView
import good.damn.ui.UITextViewSemi
import good.damn.ui.animation.UIAnimationCornerRadius
import good.damn.ui.animation.UIAnimationGroup
import good.damn.ui.animation.UIAnimationScale
import good.damn.ui.extensions.getFont
import good.damn.ui.extensions.setTextSizePx
import good.damn.ui.extensions.setTypefaceId
import good.damn.ui.layouts.UILinearLayoutVertical
import good.damn.ui.textfield.UITextField
import good.damn.ui.theme.UITheme

class EZViewLogin(
    context: Context
): UILinearLayoutVertical(
    context
) {

    override fun applyTheme(
        theme: UITheme
    ) = Unit

    init {
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
                R.string.welcomeBack
            )

            setTypefaceId(
                R.font.open_sans_extra_bold
            )

            setTextSizePx(
                fieldHeight * 0.35f
            )

            applyTheme(
                EZApp.theme
            )

            boundsFrame(
                gravity = Gravity.CENTER_HORIZONTAL,
                top = margin * 2.25f
            )

            addView(
                this
            )
        }

        UITextViewSemi(
            context
        ).apply {

            setText(
                R.string.letCallSomeDudes
            )

            alpha = 0.6f

            setTypefaceId(
                R.font.open_sans_semi_bold
            )

            setTextSizePx(
                fieldHeight * 0.2f
            )

            applyTheme(
                EZApp.theme
            )

            boundsFrame(
                gravity = Gravity.CENTER_HORIZONTAL,
                top = margin * 0.7f
            )

            addView(
                this
            )
        }

        UITextViewSemi(
            context
        ).apply {

            setText(
                R.string.accountInformation
            )

            setTextSizePx(
                fieldHeight * 0.2f
            )

            setTypefaceId(
                R.font.open_sans_extra_bold
            )

            isAllCaps = true

            applyTheme(
                EZApp.theme
            )

            boundsFrame(
                start = (EZApp.width - fieldWidth) * 0.5f,
                top = margin * 2.25f
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

            boundsFrame(
                gravity = Gravity.CENTER_HORIZONTAL,
                width = fieldWidth,
                height = fieldHeight,
                top = margin
            )

            addView(
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

            transformationMethod = PasswordTransformationMethod()

            applyTheme(
                EZApp.theme
            )

            boundsFrame(
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

            isClippedBounds = true

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

            setupVibration()

            boundsFrame(
                gravity = Gravity.CENTER_HORIZONTAL,
                width = fieldWidth,
                height = EZApp.height * 0.07f,
                top = margin * 1.5f
            )

            addView(
                this
            )
        }

    }

    private inline fun onClickBtnLogin(
        btn: UIButton,
        fieldEmail: UITextField,
        fieldPassword: UITextField
    ) {
        val hasError = Datable()

        if (checkFieldPattern(
            fieldEmail,
            EZApp.patternEmail,
            hasError
        ) || checkFieldPattern(
            fieldPassword,
            EZApp.patternPassword,
            hasError
        ) || hasError.b) {
            return
        }

        fieldEmail.tintColor = EZApp.theme.colorCorrect
        fieldPassword.tintColor = fieldEmail.tintColor

        Handler(
            Looper.getMainLooper()
        ).apply {
            btn.isEnabled = false
            fieldEmail.isEnabled = false
            fieldPassword.isEnabled = false

            btn.changeTextAnimated(
                context.getString(
                    R.string.connectToServer
                )
            )

            postDelayed({
                btn.changeTextAnimated(
                    context.getString(
                        R.string.gettingData
                    )
                )
                postDelayed({
                    btn.changeTextAnimated(
                        context.getString(
                            R.string.loginToAccount
                        )
                    )
                    btn.isEnabled = true
                    fieldEmail.isEnabled = true
                    fieldPassword.isEnabled = true
                }, 2500)
            }, 2500)

        }
    }

    private fun checkFieldPattern(
        field: UITextField,
        pattern: EZPattern,
        d: Datable
    ): Boolean {
        field.clearFocus()
        field.text?.toString()?.apply {
            if (pattern.matchesPattern(this)) {
                return false
            }
        }

        field.error(
            EZApp.theme
        )
        d.b = true

        return false
    }


    private data class Datable(
        var b: Boolean = false
    )

}
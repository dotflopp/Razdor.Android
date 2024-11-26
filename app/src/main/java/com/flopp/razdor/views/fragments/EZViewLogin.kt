package com.flopp.razdor.views.fragments

import android.content.Context
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.view.Gravity
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.flopp.razdor.EZApp
import com.flopp.razdor.R
import com.flopp.razdor.extensions.view.boundsFrame
import com.flopp.razdor.extensions.view.boundsLinear
import good.damn.ui.UIButton
import good.damn.ui.UITextView
import good.damn.ui.UITextViewSemi
import good.damn.ui.UIViewShaper
import good.damn.ui.animation.UIAnimationCornerRadius
import good.damn.ui.animation.UIAnimationGroup
import good.damn.ui.animation.UIAnimationScale
import good.damn.ui.components.shapes.UICanvasCircle
import good.damn.ui.components.shapes.UICanvasRectRound
import good.damn.ui.extensions.getFont
import good.damn.ui.extensions.setTextSizePx
import good.damn.ui.extensions.setTypefaceId
import good.damn.ui.textfield.UITextField

class EZViewLogin(
    context: Context
): FrameLayout(
    context
) {

    init {
        setBackgroundColor(
            EZApp.theme.colorBackground
        )

        addView(
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
            }
        )

        addView(
            initContentView(
                context
            )
        )

    }

    private inline fun initContentView(
        context: Context
    ) = LinearLayout(
        context
    ).apply {

        background = null

        orientation = LinearLayout.VERTICAL

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

            boundsLinear(
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
                R.string.letCallsSomeDudes
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

            boundsLinear(
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
                top = margin * 1.5f
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
                }, 2500)
            }, 2500)

        }
    }
}
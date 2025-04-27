package com.flopp.razdor.fragments.auth

import android.content.Context
import android.text.method.PasswordTransformationMethod
import android.view.Gravity
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import com.flopp.razdor.EZApp
import com.flopp.razdor.R
import com.flopp.razdor.enums.EZEnumStateAuth
import com.flopp.razdor.extensions.ui.setupVibration
import com.flopp.razdor.extensions.view.boundsFrame
import com.flopp.razdor.fragments.auth.interfaces.EZListenerOnSignInSuccess
import com.flopp.razdor.model.EZModelUser
import com.flopp.razdor.network.http.clients.EZClientHttp
import com.flopp.razdor.network.http.listeners.EZIObserverHttpOnAuth
import com.flopp.razdor.patterns.EZPattern
import good.damn.ui.UITextView
import good.damn.ui.UITextViewSemi
import good.damn.ui.animation.UIAnimationCornerRadius
import good.damn.ui.animation.UIAnimationGroup
import good.damn.ui.animation.UIAnimationScale
import good.damn.ui.buttons.UIButton
import good.damn.ui.extensions.getFont
import good.damn.ui.extensions.setTextSizePx
import good.damn.ui.extensions.setTypefaceId
import good.damn.ui.layouts.UILinearLayoutVertical
import good.damn.ui.textfield.UITextField
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class EZFragmentSignIn
: EZPageableFragment(), EZIObserverHttpOnAuth {

    var onSignInSuccess: EZListenerOnSignInSuccess? = null

    private val mClientHttp = EZClientHttp()
    private val mScopeLogin = CoroutineScope(
        Dispatchers.IO
    )

    private var mBtnSignIn: UIButton? = null
    private var mFieldUsername: UITextField? = null
    private var mFieldEmail: UITextField? = null
    private var mFieldPassword: UITextField? = null
    private var mFieldConfirmPassword: UITextField? = null

    override fun onCreateView(
        context: Context
    ) = UILinearLayoutVertical(
        context
    ).apply {
        background = null

        setPadding(
            0,
            EZApp.insetTop.toInt(),
            0,
            0
        )

        val margin = EZApp.height * 0.02f
        val fieldHeight = EZApp.height * 0.09f
        val fieldWidth = EZApp.width * 0.85f
        val strokeWidthh = fieldHeight * 0.04f

        val typefaceFieldHint = context.getFont(
            R.font.open_sans_bold
        )

        UITextView(
            context
        ).apply{

            setText(
                R.string.helloStarted
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

        UITextView(
            context
        ).apply {

            setText(
                R.string.signInToServerDudes
            )

            setTypefaceId(
                R.font.open_sans_semi_bold
            )

            setTextSizePx(
                fieldHeight * 0.25f
            )

            applyTheme(
                EZApp.theme
            )

            boundsFrame(
                gravity = Gravity.CENTER_HORIZONTAL,
                top = margin * 1.25f
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

        // поля ввода

        mFieldUsername = UITextField(
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

        mFieldEmail = UITextField(
            context
        ).apply {

            subhint = context.getString(
                R.string.warningEmail
            )

            hint = context.getString(
                R.string.email
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

        mFieldPassword = UITextField(
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

        mFieldConfirmPassword = UITextField(
            context
        ).apply {

            hint = context.getString(
                R.string.confirmPassword
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

        mBtnSignIn = UIButton(
            context
        ).apply {

            isClippedBounds = true

            text = context.getString(
                R.string.signin
            )

            typeface = context.getFont(
                R.font.open_sans_bold
            )

            setOnClickListener {
                onClickBtnSignIn(
                    this,
                    mFieldUsername!!,
                    mFieldEmail!!,
                    mFieldPassword!!,
                    mFieldConfirmPassword!!
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

    private inline fun onClickBtnSignIn(
        btn: UIButton,
        fieldUsername: UITextField,
        fieldEmail: UITextField,
        fieldPassword: UITextField,
        fieldConfirmPassword: UITextField,
    ){
        val context = btn.context
        val hasError = Datable()

        if (checkFieldPattern(fieldUsername, EZApp.patternUsername, hasError)
            ||checkFieldPattern(fieldEmail, EZApp.patternEmail, hasError)
            || checkFieldPattern(fieldPassword, EZApp.patternPassword, hasError)
            || checkFieldPattern(fieldConfirmPassword, EZApp.patternPassword, hasError)
            || checkLikenessPassword(fieldPassword, fieldConfirmPassword, hasError)
            || hasError.b) {
            return
        }

        fieldUsername.tintColor = EZApp.theme.colorCorrect
        fieldEmail.tintColor = EZApp.theme.colorCorrect
        fieldPassword.tintColor = fieldEmail.tintColor
        fieldConfirmPassword.tintColor = fieldEmail.tintColor

        val username = fieldUsername.text!!.toString()
        val email = fieldEmail.text!!.toString()
        val password = fieldPassword.text!!.toString()
        //val confirmPassword = fieldConfirmPassword.text!!.toString()

        btn.isEnabled = false
        fieldUsername.isEnabled = false
        fieldEmail.isEnabled = false
        fieldPassword.isEnabled = false
        fieldConfirmPassword.isEnabled = false

        btn.changeTextAnimated(
            context.getString(
                R.string.connectToServer
            )
        )

        mClientHttp.login(
            email,
            password,
            this@EZFragmentSignIn,
            mScopeLogin
        )

    }

    private fun checkLikenessPassword(
        field1: UITextField,
        field2: UITextField,
        d: Datable
    ): Boolean{
        field1.clearFocus()
        field2.clearFocus()

        if(field1.text?.toString()==field2.text?.toString()) {
            return false
        }

        field1.error(
            EZApp.theme
        )
        field2.error(
            EZApp.theme
        )
        d.b = true

        return false
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

    override suspend fun onAuthSuccess(
        user: EZModelUser
    ) {
        withContext(
            Dispatchers.Main
        ) {
            mBtnSignIn?.apply {
                changeTextAnimated(
                    context.getString(
                        R.string.loginToAccount
                    )
                )
            }

            delay(1750)

            onSignInSuccess?.onSignInSuccess(
                user
            )
        }
    }

    override suspend fun onAuthFailed(
        state: EZEnumStateAuth
    ) {
        withContext(
            Dispatchers.Main
        ) {
            mBtnSignIn?.apply {
                changeTextAnimated(
                    context.getString(
                        R.string.signin
                    )
                )
                isEnabled = true
                startShakeAnimation()
            }

            mFieldPassword?.apply {
                isEnabled = true
                error(
                    EZApp.theme
                )
            }

            mFieldUsername?.apply {
                isEnabled = true
                error(
                    EZApp.theme
                )
            }
        }
    }
}


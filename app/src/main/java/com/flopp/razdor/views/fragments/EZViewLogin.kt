package com.flopp.razdor.views.fragments

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.widget.LinearLayout
import com.flopp.razdor.EZApp
import com.flopp.razdor.R
import com.flopp.razdor.extensions.view.boundsLinear
import good.damn.ui.UIButton
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

        val editTextUsername = UITextField(
            context
        ).apply {

            val h = EZApp.height * 0.1f

            hint = context.getString(
                R.string.username
            )

            cornerRadiusFactor = 0.3f

            strokeWidth = h * 0.013f

            applyTheme(
                EZApp.theme
            )

            boundsLinear(
                width = -1f,
                height = h
            )

            this@EZViewLogin.addView(
                this
            )
        }

        val editTextPassword = UITextField(
            context
        ).apply {

            val h = EZApp.height * 0.1f

            hint = context.getString(
                R.string.password
            )

            cornerRadiusFactor = 0.3f
            strokeWidth = h * 0.013f

            applyTheme(
                EZApp.theme
            )

            boundsLinear(
                width = -1f,
                height = h
            )

            addView(
                this
            )
        }

        UIButton(
            context
        ).apply {

            setTextId(
                R.string.login
            )

            setOnClickListener {
                onClickBtnLogin(
                    editTextUsername.text.toString(),
                    editTextPassword.text.toString()
                )
            }

            textSizeFactor = 0.35f
            cornerRadiusFactor = 0.35f

            typeface = Typeface.DEFAULT_BOLD

            applyTheme(
                EZApp.theme
            )

            boundsLinear(
                gravity = Gravity.CENTER_HORIZONTAL,
                width = EZApp.width * 0.75f,
                height = EZApp.height * 0.08f
            )

            addView(
                this
            )
        }

    }


    private inline fun onClickBtnLogin(
        username: String,
        password: String
    ) {
        if (username.isBlank() || password.isBlank()) {

            return
        }



    }
}
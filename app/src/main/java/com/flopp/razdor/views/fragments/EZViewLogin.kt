package com.flopp.razdor.views.fragments

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatEditText
import com.flopp.razdor.EZApp
import com.flopp.razdor.R
import com.flopp.razdor.extensions.view.boundsLinear
import good.damn.ui.UIButton
import good.damn.ui.UITextField
import good.damn.ui.extensions.applyTheme

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

            hint = context.getString(
                R.string.username
            )

            tintColor = EZApp.theme.colorText

            /*cornerRadiusFactor = 0.25f

            applyTheme(
                EZApp.theme
            )*/

            boundsLinear(
                width = -1f,
                height = EZApp.height * 0.1f
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

            tintColor = EZApp.theme.colorText

            boundsLinear(
                width = -1f,
                height = EZApp.height * 0.1f
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
                    "Username",//editTextUsername.text.toString(),
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
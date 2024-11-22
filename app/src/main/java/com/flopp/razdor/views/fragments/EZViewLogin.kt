package com.flopp.razdor.views.fragments

import android.content.Context
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatEditText
import com.flopp.razdor.EZApp
import com.flopp.razdor.R
import com.flopp.razdor.extensions.toast
import com.flopp.razdor.extensions.view.boundsLinear
import good.damn.ui.UIButton
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

        val editTextUsername = AppCompatEditText(
            context
        ).apply {

            setHint(
                R.string.username
            )

            applyTheme(
                EZApp.theme
            )

            boundsLinear(
                width = -1f
            )

            addView(
                this
            )
        }

        val editTextPassword = AppCompatEditText(
            context
        ).apply {

            setHint(
                R.string.password
            )

            boundsLinear(
                width = -1f
            )

            applyTheme(
                EZApp.theme
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

            textSizeFactor = 0.25f

            applyTheme(
                EZApp.theme
            )

            boundsLinear(
                width = -1f,
                height = EZApp.height * 0.25f
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
            context.toast(
                R.string.credentialsBlank
            )
            return
        }



    }
}
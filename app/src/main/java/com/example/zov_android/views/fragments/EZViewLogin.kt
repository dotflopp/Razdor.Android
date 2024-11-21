package com.example.zov_android.views.fragments

import android.content.Context
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import com.example.zov_android.EZApp
import com.example.zov_android.R
import com.example.zov_android.extensions.toast
import com.example.zov_android.extensions.view.applyTheme
import com.example.zov_android.extensions.view.boundsLinear
import good.damn.ui.UIButton

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

            applyTheme()

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

            applyTheme()

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
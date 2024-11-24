package com.flopp.razdor.views.fragments

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatEditText
import com.flopp.razdor.EZApp
import com.flopp.razdor.R
import com.flopp.razdor.extensions.toast
import com.flopp.razdor.extensions.toastRoot
import com.flopp.razdor.extensions.view.boundsLinear
import com.flopp.razdor.fragments.navigation.EZFragmentNavigation
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
package com.example.zov_android.views

import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import com.example.zov_android.EZApp
import com.example.zov_android.R
import com.example.zov_android.extensions.toast
import com.example.zov_android.extensions.view.boundsFrame
import com.example.zov_android.extensions.view.boundsLinear

class EZViewLogin(
    context: Context
): LinearLayout(
    context
) {

    init {
        setBackgroundColor(
            0xffffffff.toInt()
        )

        orientation = VERTICAL

        val editTextUsername = AppCompatEditText(
            context
        ).apply {

            hint = "Username"

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

            hint = "Password"

            boundsLinear(
                width = -1f
            )

            addView(
                this
            )
        }

        AppCompatButton(
            context
        ).apply {
            text = "Login"

            setOnClickListener {
                onClickBtnLogin(
                    editTextUsername.text.toString(),
                    editTextPassword.text.toString()
                )
            }

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
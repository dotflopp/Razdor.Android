package com.example.zov_android.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.zov_android.views.EZViewLogin

class EZActivityMain
: AppCompatActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(
            savedInstanceState
        )

        window.setBackgroundDrawable(null)

        setContentView(
            EZViewLogin(
                this
            )
        )
    }

}
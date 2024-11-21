package com.example.zov_android.extensions.view

import androidx.appcompat.widget.AppCompatEditText
import com.example.zov_android.EZApp

fun AppCompatEditText.applyTheme() = EZApp.theme.apply {
    setHintTextColor(
        colorHint
    )

    setTextColor(
        colorText
    )
}
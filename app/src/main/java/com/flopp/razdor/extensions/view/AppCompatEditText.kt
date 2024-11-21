package com.flopp.razdor.extensions.view

import androidx.appcompat.widget.AppCompatEditText
import com.flopp.razdor.EZApp

fun AppCompatEditText.applyTheme() = EZApp.theme.apply {
    setHintTextColor(
        colorHint
    )

    setTextColor(
        colorText
    )
}
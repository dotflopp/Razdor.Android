package com.flopp.razdor.repos

import android.content.Context
import android.os.Vibrator
import android.view.inputmethod.InputMethodManager

class EZRepoServices(
    context: Context
) {
    val vibrator = context.getSystemService(
        Context.VIBRATOR_SERVICE
    ) as Vibrator

    val inputMethodManager = context.getSystemService(
        Context.INPUT_METHOD_SERVICE
    ) as InputMethodManager

}
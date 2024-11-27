package com.flopp.razdor.services

import android.content.Context
import android.inputmethodservice.InputMethodService
import android.os.Vibrator
import android.view.inputmethod.InputMethodManager

class EZServicesSystem(
    context: Context
) {
    val vibrator = context.getSystemService(
        Context.VIBRATOR_SERVICE
    ) as Vibrator

    val inputMethodManager = context.getSystemService(
        Context.INPUT_METHOD_SERVICE
    ) as InputMethodManager

}
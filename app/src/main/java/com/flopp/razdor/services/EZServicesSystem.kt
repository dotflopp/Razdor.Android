package com.flopp.razdor.services

import android.content.Context
import android.os.Vibrator

class EZServicesSystem(
    context: Context
) {
    val vibrator = context.getSystemService(
        Context.VIBRATOR_SERVICE
    ) as Vibrator
}
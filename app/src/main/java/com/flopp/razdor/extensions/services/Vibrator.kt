package com.flopp.razdor.extensions.services

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

inline fun Vibrator.vibrateCompat(
    ms: Long
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrate(
            VibrationEffect.createOneShot(
                ms,
                VibrationEffect.DEFAULT_AMPLITUDE
            )
        )
        return
    }

    vibrate(ms)
}
package com.example.zov_android.utils

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

fun AppCompatActivity.getCameraAndMicPermission(success: () -> Unit) {
    val permissions = mutableListOf<String>()

    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
        permissions.add(Manifest.permission.CAMERA)
    }
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
        permissions.add(Manifest.permission.RECORD_AUDIO)
    }
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE_DATA_SYNC) != PackageManager.PERMISSION_GRANTED) {
        permissions.add(Manifest.permission.FOREGROUND_SERVICE_DATA_SYNC)
    }

    if (permissions.isNotEmpty()) {
        ActivityCompat.requestPermissions(this, permissions.toTypedArray(), 101) // Using a generic request code
    } else {
        success()
    }
}

fun Int.convertToHumanTime() : String{
    val seconds = this%60
    val minutes = this/60
    val secondsString = if (seconds<10) "0$seconds" else "$seconds"
    val minutesString = if (minutes < 10) "0$minutes" else "$minutes"
    return "$minutesString:$secondsString"
}
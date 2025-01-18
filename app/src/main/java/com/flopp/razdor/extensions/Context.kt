package com.flopp.razdor.extensions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat
import com.flopp.razdor.activities.EZActivityMain


inline fun Activity.checkPermissionCamera(): Boolean {
    if (ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED) {
        return true
    }

    ActivityCompat.requestPermissions(
        this,
        arrayOf(
            Manifest.permission.CAMERA
        ),
        1
    )

    return false
}

inline fun Activity.checkPermissionMicrophone(): Boolean {
    if (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED) {
        return true
    }

    ActivityCompat.requestPermissions(
        this,
        arrayOf(
            Manifest.permission.RECORD_AUDIO
        ),
        1
    )

    return false
}

inline fun Context.toastRoot(
    msg: String
) = mainActivity().toast(msg)

inline fun Context.toastRoot(
    @StringRes id: Int
) = mainActivity().toast(id)

inline fun Context.mainActivity() =
    this as EZActivityMain

inline fun Context.toast(
    @StringRes id: Int,
    length: Int = Toast.LENGTH_SHORT
) = toast(
    getString(
        id
    ),
    length
)

inline fun Context.toast(
    msg: String,
    length: Int = Toast.LENGTH_SHORT
) = Toast.makeText(
    this,
    msg,
    length
).show()
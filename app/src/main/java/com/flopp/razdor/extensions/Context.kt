package com.flopp.razdor.extensions

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

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
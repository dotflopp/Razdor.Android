package com.flopp.razdor.extensions

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import com.flopp.razdor.activities.EZActivityMain

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
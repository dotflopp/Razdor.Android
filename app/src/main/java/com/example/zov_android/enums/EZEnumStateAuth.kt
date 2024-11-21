package com.example.zov_android.enums

import androidx.annotation.StringRes
import com.example.zov_android.R

enum class EZEnumStateAuth(
    @StringRes val stringId: Int
) {
    USER_NOT_FOUND(R.string.errorUserNotFound),
    ALREADY_REGISTERED(R.string.errorUserAlreadExists),
}
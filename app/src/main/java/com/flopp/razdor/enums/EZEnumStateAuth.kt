package com.flopp.razdor.enums

import androidx.annotation.StringRes
import com.flopp.razdor.R

enum class EZEnumStateAuth(
    @StringRes val stringId: Int
) {
    USER_NOT_FOUND(R.string.errorUserNotFound),
    ALREADY_REGISTERED(R.string.errorUserAlreadExists),
}
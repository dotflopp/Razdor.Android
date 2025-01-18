package com.flopp.razdor.fragments.auth.interfaces

import com.flopp.razdor.model.EZModelUser

interface EZListenerOnLoginSuccess {
    fun onLoginSuccess(
        user: EZModelUser
    )
}
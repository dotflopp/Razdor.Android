package com.flopp.razdor.fragments.auth.interfaces

import com.flopp.razdor.model.EZModelUser

interface EZListenerOnSignInSuccess {
    fun onSignInSuccess(
        user: EZModelUser
    )

}
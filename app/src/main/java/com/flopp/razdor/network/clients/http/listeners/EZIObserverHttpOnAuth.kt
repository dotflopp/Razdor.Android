package com.flopp.razdor.network.clients.http.listeners

import com.flopp.razdor.enums.EZEnumStateAuth
import com.flopp.razdor.model.EZModelUser

interface EZIObserverHttpOnAuth {
    fun onAuthSuccess(
        user: EZModelUser
    )

    fun onAuthFailed(
        state: EZEnumStateAuth
    )
}
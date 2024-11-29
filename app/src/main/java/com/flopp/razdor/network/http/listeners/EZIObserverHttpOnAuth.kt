package com.flopp.razdor.network.http.listeners

import com.flopp.razdor.enums.EZEnumStateAuth
import com.flopp.razdor.model.EZModelUser

interface EZIObserverHttpOnAuth {
    suspend fun onAuthSuccess(
        user: EZModelUser
    )

    suspend fun onAuthFailed(
        state: EZEnumStateAuth
    )
}
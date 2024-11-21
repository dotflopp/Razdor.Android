package com.example.zov_android.network.clients.http.listeners

import com.example.zov_android.enums.EZEnumStateAuth
import com.example.zov_android.model.EZModelUser

interface EZIObserverHttpOnAuth {
    fun onAuthSuccess(
        user: EZModelUser
    )

    fun onAuthFailed(
        state: EZEnumStateAuth
    )
}
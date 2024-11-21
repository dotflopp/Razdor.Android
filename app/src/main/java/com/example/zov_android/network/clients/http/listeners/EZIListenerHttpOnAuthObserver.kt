package com.example.zov_android.network.clients.http.listeners

import com.example.zov_android.enums.EZEnumStateAuth

interface EZIListenerHttpOnAuthObserver {
    fun onAuthSuccess(
        username: String,
        id: String,
        token: String
    )

    fun onAuthFailed(
        state: EZEnumStateAuth
    )
}
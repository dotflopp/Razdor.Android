package com.example.zov_android.network.clients.http

import com.example.zov_android.EZApp
import com.example.zov_android.network.clients.base.EZClientBase
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class EZClientHttp(
    override val id: String,
    override val username: String?
): EZClientBase {

    companion object {
        private const val URL_LOGIN = "${EZApp.rootUrl}/login"
        private const val URL_SIGN_IN = "${EZApp.rootUrl}/signIn"
    }

    private val mClient = OkHttpClient()

    fun login() = Request.Builder()
        .url(URL_LOGIN)
        .post(
            "".toRequestBody()
        ).build().apply {
            mClient.newCall(
                this
            ).execute()
        }

    fun signIn() = Request.Builder()
        .url(URL_SIGN_IN)
        .post(
            "".toRequestBody()
        ).build().apply {
            mClient.newCall(
                this
            ).execute()
        }

}
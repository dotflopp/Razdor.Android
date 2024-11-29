package com.flopp.razdor.network.http.clients

import com.flopp.razdor.EZApp
import com.flopp.razdor.enums.EZEnumStateAuth
import com.flopp.razdor.extensions.json.toUser
import com.flopp.razdor.extensions.okhttp.toJSON
import com.flopp.razdor.network.http.base.EZClientBase
import com.flopp.razdor.network.http.clients.listeners.EZIObserverHttpOnAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class EZClientHttp(
    override val id: String,
    override val username: String?,
): EZClientBase {

    companion object {
        private const val URL_LOGIN = "${EZApp.rootUrl}/login"
        private const val URL_SIGN_IN = "${EZApp.rootUrl}/signIn"
    }

    private val mClient = OkHttpClient()

    fun login(
        withObserver: EZIObserverHttpOnAuth? = null,
        scope: CoroutineScope
    ) = scope.launch {
        val request = Request.Builder()
            .url(URL_LOGIN)
            .post(
                "".toRequestBody()
            ).build()

        process(
            withObserver,
            request
        )
    }

    fun signIn(
        withObserver: EZIObserverHttpOnAuth?,
        scope: CoroutineScope
    ) = scope.launch {
        val request = Request.Builder()
            .url(URL_SIGN_IN)
            .post(
                "".toRequestBody()
            ).build()

        process(
            withObserver,
            request
        )
    }

    private fun process(
        withObserver: EZIObserverHttpOnAuth?,
        request: Request
    ) {
        val response = mClient.newCall(
            request
        ).execute()

        val user = response
            .body
            ?.toJSON()
            ?.toUser()

        if (user == null) {
            withObserver?.onAuthFailed(
                EZEnumStateAuth.USER_NOT_FOUND
            )
            return
        }

        withObserver?.onAuthSuccess(
            user
        )
    }
}
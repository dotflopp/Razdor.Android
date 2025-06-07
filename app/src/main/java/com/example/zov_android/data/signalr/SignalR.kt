package com.example.zov_android.data.signalr


import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.zov_android.data.models.response.MessagesResponse
import com.example.zov_android.di.qualifiers.Token
import com.microsoft.signalr.Action1
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.TransportEnum
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class SignalR @Inject constructor(
    @ApplicationContext private val context: Context,
    private val url: String
) {

    private var currentToken: String? = null

    val connection: HubConnection = HubConnectionBuilder.create(url)
        .withTransport(TransportEnum.WEBSOCKETS)
        .shouldSkipNegotiate(true)
        .build()

    // LiveData для отправки новых сообщений во ViewModel
    private val _newMessageEvent = MutableLiveData<MessagesResponse>()
    val newMessageEvent: LiveData<MessagesResponse> get() = _newMessageEvent

    private var reconnectAttempts = 0
    private val maxReconnectAttempts = 5
    private val reconnectDelayMillis = 5000L

    init {
        setupMessageHandlers()
        setupConnectionHandler()
    }

    private fun setupMessageHandlers() {
        connection.on("MessageCreated") { message: MessagesResponse ->
            _newMessageEvent.postValue(message)
        }
    }

    private fun setupConnectionHandler() {
        connection.onClosed { error ->
            Log.d("SignalR", "Соединение закрыто${error?.let { ": ${it.message}" } ?: ""}")
            if (currentToken != null) {
                attemptReconnect()
            }
        }
    }

    suspend fun startConnection(token:String) {

        this.currentToken = token

        reconnectAttempts = 0 // Сброс счётчика при новом старте

        connection.setBaseUrl("$url?access-token=$token")
        try {
            Log.d("SignalR", "Подключение к $url...")
            connection.start()
            Log.d("SignalR", "Успешно подключено. ID соединения: ${connection.connectionId}")
        } catch (e: Exception) {
            Log.e("SignalR", "Ошибка подключения", e)
            attemptReconnect()
        }
    }

    private fun attemptReconnect() {
        if (reconnectAttempts >= maxReconnectAttempts) {
            Log.w("SignalR", "Достигнуто максимальное число попыток переподключения")
            return
        }

        reconnectAttempts++

        Log.d("SignalR", "Попытка переподключения №$reconnectAttempts")

        // Используем coroutineScope для задержки
        CoroutineScope(Dispatchers.IO).launch {
            delay(reconnectDelayMillis)
            currentToken?.let { token ->
                startConnection(token)
            }
        }
    }

    suspend fun stopConnection() {
        connection.stop()
    }
}

private inline fun<reified T> HubConnection.on(target: String, callback: Action1<T>) {
    this.on(target, callback, T::class.java)
}


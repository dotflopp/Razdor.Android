package com.example.zov_android.data.signalr


import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.zov_android.data.models.response.ChannelResponse
import com.example.zov_android.data.models.response.MessagesResponse
import com.example.zov_android.data.models.response.UserResponse
import com.example.zov_android.di.qualifiers.Token
import com.microsoft.signalr.Action1
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
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

    private var hubConnection: HubConnection? = null
    private var currentToken: String? = null

    private var isReconnecting = false

    private var connection: HubConnection = HubConnectionBuilder.create(url)
        .withTransport(TransportEnum.WEBSOCKETS)
        .shouldSkipNegotiate(true)
        .build()

    // LiveData для отправки новых сообщений во ViewModel
    private val _newMessageEvent = MutableLiveData<MessagesResponse>()

    private val _newChannelEvent = MutableLiveData<ChannelResponse>()

    private val _newMemberEvent = MutableLiveData<UserResponse>()

    val newMessageEvent: LiveData<MessagesResponse> get() = _newMessageEvent
    val newChannelEvent: LiveData<ChannelResponse> get() = _newChannelEvent

    val newMemberEvent: LiveData<UserResponse> get() = _newMemberEvent

    private var reconnectAttempts = 0
    private val maxReconnectAttempts = 5
    private val reconnectDelayMillis = 5000L

    init {
        setupMessageHandlers()
        setupConnectionHandler()
        setupChannelHandlers()
        setupMemberHandlers()
    }

    private fun setupMemberHandlers() {
        connection.on("MemberChanged") { user: UserResponse ->
            _newMemberEvent.postValue(user)
        }
    }

    private fun setupMessageHandlers() {
        connection.on("MessageCreated") { message: MessagesResponse ->
            _newMessageEvent.postValue(message)
        }
    }

    private fun setupChannelHandlers(){
        connection.on("ChannelCreated") { channel: ChannelResponse ->
            _newChannelEvent.postValue(channel)
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

        // Проверяем необходимость переподключения
        if (currentToken == token && isConnected()) {
            Log.d("SignalR", "Already connected with same token")
            return
        }

        reconnectAttempts = 0 // Сброс счётчика при новом старте

        stopConnection()
        currentToken = token
        createNewConnection()

        connection.setBaseUrl("$url?access-token=$token")
        try {
            Log.d("SignalR", "Подключение к $url...")
            connection.start()?.blockingAwait()
            isReconnecting = false
            Log.d("SignalR", "Успешно подключено. ID соединения: ${connection.connectionId}")
        } catch (e: Exception) {
            Log.e("SignalR", "Ошибка подключения", e)
            attemptReconnect()
        }
    }

    private fun createNewConnection() {
         connection = HubConnectionBuilder.create(url)
            .withTransport(TransportEnum.WEBSOCKETS)
            .shouldSkipNegotiate(true)
            .build()

        setupMessageHandlers()
        setupConnectionHandler()
        setupChannelHandlers()
        setupMemberHandlers()
    }

    private fun attemptReconnect() {
        if (reconnectAttempts >= maxReconnectAttempts) {
            Log.w("SignalR", "Достигнуто максимальное число попыток переподключения")
            return
        }
        isReconnecting = true
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

    private suspend fun stopConnection() {
        connection.stop()
    }

    private fun isConnected(): Boolean {
        return hubConnection?.connectionState == HubConnectionState.CONNECTED
    }
}

private inline fun<reified T> HubConnection.on(target: String, callback: Action1<T>) {
    this.on(target, callback, T::class.java)
}


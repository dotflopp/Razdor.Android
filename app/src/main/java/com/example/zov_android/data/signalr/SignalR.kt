package com.example.zov_android.data.signalr


import android.content.Context
import android.util.Log
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import com.microsoft.signalr.TransportEnum
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SignalR @Inject constructor(
    @ApplicationContext private val context: Context,
    private val url: String
) {
    val connection: HubConnection = HubConnectionBuilder.create(url)
        .withTransport(TransportEnum.WEBSOCKETS)
        .shouldSkipNegotiate(true)
        .build()

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var reconnectAttempts = 0

    init {
        // Обработчик закрытия соединения
        connection.onClosed { error ->
            Log.e("SignalR", "Соединение закрыто: ${error?.message}")
            startAutoReconnect()
        }

        // Добавляем обработчик для диагностики
        connection.on("Diagnostics", { message: String ->
            Log.d("SignalR-Debug", "Диагностика: $message")
        }, String::class.java)
    }

    suspend fun startConnection() {
        try {
            Log.d("SignalR", "Подключение к $url...")
            coroutineScope.launch {
                connection.start()
            }
            reconnectAttempts = 0
            Log.d("SignalR", "Успешно подключено. ID соединения: ${connection.connectionId}")

            // Запрос диагностической информации
            //requestConnectionInfo()
        } catch (e: Exception) {
            Log.e("SignalR", "Ошибка подключения", e)
            startAutoReconnect()
        }
    }

    // Запрос диагностической информации с сервера
    private suspend fun requestConnectionInfo() {
        try {
            withContext(Dispatchers.IO) {
                connection.invoke("GetConnectionInfo")
            }
            Log.d("SignalR-Info", "Запрос информации отправлен")
        } catch (e: Exception) {
            Log.w("SignalR", "Не удалось получить диагностику", e)
        }
    }

    private fun startAutoReconnect() {
        coroutineScope.launch {
            reconnectAttempts++
            val delayTime = when {
                reconnectAttempts > 10 -> 60_000L // 1 минута после 10 попыток
                reconnectAttempts > 5 -> 30_000L  // 30 секунд после 5 попыток
                else -> 5_000L                    // 5 секунд для первых попыток
            }

            Log.w("SignalR", "Попытка переподключения #$reconnectAttempts через ${delayTime}ms")
            delay(delayTime)

            try {
                if (connection.connectionState != HubConnectionState.CONNECTED) {
                    startConnection()
                }
            } catch (e: Exception) {
                Log.e("SignalR", "Ошибка при переподключении", e)
                startAutoReconnect()
            }
        }
    }

    suspend fun sendSafe(method: String, vararg args: Any) {
        try {
            if (connection.connectionState != HubConnectionState.CONNECTED) {
                Log.w("SignalR", "Соединение не активно. Переподключаемся...")
                startConnection()
                delay(500) // Даем время на подключение
            }

            Log.d("SignalR", "Отправка '$method' с аргументами: ${args.joinToString()}")
            coroutineScope.launch {
                connection.send(method, *args)
            }
            Log.d("SignalR", "Сообщение '$method' успешно отправлено")
        } catch (e: Exception) {
            Log.e("SignalR", "Ошибка отправки '$method'", e)
        }
    }

    // Добавляем возможность подписки на события
    fun <T : Any> on(
        method: String,
        callback: (T) -> Unit,
        clazz: Class<T>
    ) {
        connection.on(method, callback, clazz)
    }


    // Проверка состояния соединения
    fun isConnected(): Boolean {
        return connection.connectionState == HubConnectionState.CONNECTED
    }

    // Закрытие соединения
    suspend fun stopConnection() {
        try {
            coroutineScope.launch {
                connection.stop()
            }
            Log.d("SignalR", "Соединение остановлено")
        } catch (e: Exception) {
            Log.e("SignalR", "Ошибка при остановке соединения", e)
        }
    }

    // Тестовый пинг
    suspend fun ping(): Boolean {
        return try {
            coroutineScope.launch {
                connection.invoke("Ping")
            }
            true
        } catch (e: Exception) {
            Log.e("SignalR", "Ошибка пинга", e)
            false
        }
    }
}
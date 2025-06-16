package com.example.zov_android.data.websocket

import android.util.Log
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import org.webrtc.IceCandidate
import java.net.URI
import java.util.LinkedList

class WebSocketClient(
    uri: URI,
    private val onMessageReceived: (String) -> Unit
) : WebSocketClient(uri) {

    private var reconnectAttempts = 0
    private val maxReconnectAttempts = 5

    private val messageQueue = LinkedList<String>()
    private var _isConnected = CompletableDeferred<Boolean>().apply { complete(false) }
    val isConnected: Deferred<Boolean> = _isConnected

    fun queueMessage(message: String) {
        messageQueue.add(message)
    }

    private fun flushQueue() {
        while (messageQueue.isNotEmpty() && isOpen) {
            send(messageQueue.poll())
        }
    }

    override fun onOpen(handshakedata: ServerHandshake?) {
        Log.d("WebSocket", "Соединение установлено")
        _isConnected.complete(true)
        flushQueue()
    }

    override fun onMessage(message: String?) {
        message?.let(onMessageReceived)
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        Log.d("WebSocket", "Соединение закрыто: $reason. Переподключение...")
        _isConnected = CompletableDeferred() // Сброс для повторного подключения
        if (reconnectAttempts < maxReconnectAttempts) {
            reconnectAttempts++
            close()
            Thread.sleep((reconnectAttempts * 1000).toLong())
            reconnect()
        }
    }

    override fun onError(ex: Exception?) {
        Log.e("WebSocket", "Ошибка: ${ex?.message}")
        _isConnected.completeExceptionally(ex ?: Throwable("Неизвестная ошибка"))
    }

    fun sendOffer(sessionId: String, offer: String) {
        val payload = JSONObject().apply {
            put("type", "offer")
            put("sessionId", sessionId)
            put("payload", offer)
        }
        send(payload.toString())
    }

    fun sendAnswer(sessionId: String, answer: String) {
        val payload = JSONObject().apply {
            put("type", "answer")
            put("sessionId", sessionId)
            put("payload", answer)
        }
        send(payload.toString())
    }

    fun sendIceCandidate(sessionId: String, candidate: IceCandidate) {
        val candidatePayload = JSONObject().apply {
            put("sdpMid", candidate.sdpMid)
            put("sdpMLineIndex", candidate.sdpMLineIndex)
            put("candidate", candidate.sdp)
        }

        val payload = JSONObject().apply {
            put("type", "ice-candidate")
            put("sessionId", sessionId)
            put("payload", candidatePayload)  // Отправляем как объект
        }
        send(payload.toString())
    }
}
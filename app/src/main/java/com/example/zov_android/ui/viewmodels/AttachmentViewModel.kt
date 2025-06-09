package com.example.zov_android.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zov_android.data.api.ApiClient
import com.example.zov_android.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class AttachmentViewModel @Inject constructor(
    private val client: OkHttpClient
) : ViewModel() {

    private val _attachmentState = MutableStateFlow<AttachmentViewState>(AttachmentViewState.Idle)
    val attachmentState: StateFlow<AttachmentViewState> = _attachmentState

    private var lastMimeType: String = "application/octet-stream"

    fun setLastUsedMimeType(mimeType: String) {
        lastMimeType = mimeType
    }

    fun getLastMimeType(): String = lastMimeType

    fun resetState() {
        _attachmentState.value = AttachmentViewState.Idle
    }

    fun downloadAttachment(
        channelId: Long,
        messageId: Long,
        attachmentId: Long,
        token: String
    ) {
        // Проверяем, не выполняется ли уже загрузка
        if (_attachmentState.value is AttachmentViewState.Loading) {
            return
        }

        Log.d("AttachmentViewModel", "Запуск загрузки вложения. ChannelId: $channelId, MessageId: $messageId, AttachmentId: $attachmentId")
        _attachmentState.value = AttachmentViewState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("https://dotflopp.ru/api/attachments/$channelId/$messageId/$attachmentId?access-token=${token}")
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw IOException("Ошибка сервера: ${response.code}")
                    }

                    val body = response.body ?: throw IOException("Пустой ответ")

                    // Копируем содержимое в байтовый буфер
                    val bytes = body.bytes()

                    // Используем ByteArrayInputStream для безопасной передачи
                    _attachmentState.value = AttachmentViewState.Success(ByteArrayInputStream(bytes))
                }
            } catch (e: Exception) {
                Log.e("AttachmentViewModel", "Ошибка загрузки файла", e)
                _attachmentState.value = AttachmentViewState.Error("Ошибка: ${e.message ?: "Неизвестная ошибка"}")
            }
        }
    }
}

sealed class AttachmentViewState {
    object Idle : AttachmentViewState()
    object Loading : AttachmentViewState()
    data class Success(val inputStream: InputStream) : AttachmentViewState()
    data class Error(val message: String) : AttachmentViewState()
}
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
import java.io.File
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
        token:String
    ) {
        Log.d("AttachmentViewModel", "Запуск загрузки вложения. ChannelId: $channelId, MessageId: $messageId, AttachmentId: $attachmentId")

        if (_attachmentState.value is AttachmentViewState.Loading) {
            return // Предотвращаем множественные вызовы
        }
        _attachmentState.value = AttachmentViewState.Loading
        val request = Request.Builder()
            .url("https://dotflopp.ru/api/attachments/$channelId/$messageId/$attachmentId?access-token=${token}")
            .build()

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    Log.d("AttachmentViewModel", "Получен ответ: ${response.body}")
                    response.body?.byteStream()?.let { stream ->
                        _attachmentState.value = AttachmentViewState.Success(stream) // Прямое обновление
                    }
                    response.body?.use { body ->
                        _attachmentState.value = AttachmentViewState.Success(body.byteStream())
                    }
                }
            } catch (e: Exception) {
                _attachmentState.value = AttachmentViewState.Error(e.message ?: "Ошибка")
            }
        }
    }
}
sealed class AttachmentViewState {
    data object Idle : AttachmentViewState()
    data object Loading : AttachmentViewState()
    data class Success(val inputStream: InputStream) : AttachmentViewState()
    data class Error(val message: String) : AttachmentViewState()
}
package com.example.zov_android.ui.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import asFlow
import com.example.zov_android.data.models.response.MessagesResponse
import com.example.zov_android.data.repository.MainRepository
import com.example.zov_android.data.signalr.SignalR
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream
import javax.inject.Inject


@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val repository: MainRepository
): ViewModel(){

    @Inject lateinit var signalR: SignalR

    private val messagesViewModel = @SuppressLint("StaticFieldLeak")
    object: BaseViewModel<MessagesResponse>(ViewState.Idle) {}

    private val messagesListViewModel = @SuppressLint("StaticFieldLeak")
    object: BaseViewModel<List<MessagesResponse>>(ViewState.Idle) {}

    private val attachmentViewModel = @SuppressLint("StaticFieldLeak")
    object: BaseViewModel<InputStream>(ViewState.Idle) {}

    // Публичные состояния
    val messagesState: StateFlow<BaseViewModel.ViewState<MessagesResponse>> = messagesViewModel.state
    val messagesListState: StateFlow<BaseViewModel.ViewState<List<MessagesResponse>>> = messagesListViewModel.state

    val attachmentViewState: StateFlow<BaseViewModel.ViewState<InputStream>> = attachmentViewModel.state



    fun addNewMessage(message: MessagesResponse) {
        val currentState = messagesListViewModel.state.value
        if (currentState is BaseViewModel.ViewState.Success && currentState.data.isNotEmpty()) {
            val newList = currentState.data.toMutableList().apply {
                add(0, message)
            }
            messagesListViewModel._state.value = BaseViewModel.ViewState.Success(newList)
        }
    }

    fun loadMessages(token:String, context: Context, channelId:Long, text:String, files:List<File>?){
        messagesViewModel.handleRequest(
            request = {repository.createMessages(token, context, channelId, text, files)},
            successHandler = {it}
        )
    }

    fun claimMessages(token:String, channelId:Long){
        messagesListViewModel.handleRequest(
            request = {repository.claimMessages(token, channelId)},
            successHandler = {it}
        )
    }

    /*fun claimAttachment(token: String, channelId: Long, messageId:Long, attachmentId:Long){
        attachmentViewModel.handleRequest(
            request = {repository.claimAttachment(token, channelId, messageId, attachmentId)},
            successHandler = {it}
        )
    }*/


}
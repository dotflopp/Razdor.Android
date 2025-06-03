package com.example.zov_android.ui.viewmodels

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zov_android.data.models.request.ChannelRequest
import com.example.zov_android.data.models.request.GuildRequest
import com.example.zov_android.data.models.response.ChannelResponse
import com.example.zov_android.data.models.response.GuildResponse
import com.example.zov_android.data.models.response.SessionResponse
import com.example.zov_android.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class GuildViewModel @Inject constructor(
    private val repository: MainRepository
): ViewModel(){

    private val guildViewModel = @SuppressLint("StaticFieldLeak")
    object: BaseViewModel<GuildResponse>(ViewState.Idle) {}

    private val channelViewModel = @SuppressLint("StaticFieldLeak")
    object: BaseViewModel<ChannelResponse>(ViewState.Idle) {}

    private val sessionViewModel = @SuppressLint("StaticFieldLeak")
    object: BaseViewModel<SessionResponse>(ViewState.Idle) {}

    // Публичные состояния
    val guildState: StateFlow<BaseViewModel.ViewState<GuildResponse>> = guildViewModel.state
    val channelState: StateFlow<BaseViewModel.ViewState<ChannelResponse>> = channelViewModel.state
    val sessionState: StateFlow<BaseViewModel.ViewState<SessionResponse>> = sessionViewModel.state

    fun loadGuildData(guildRequest: GuildRequest){
        guildViewModel.handleRequest(
            request = {repository.postCreateGuild(guildRequest)},
            successHandler = { it },
        )
    }

    fun loadChannelData(guildId: Long, channelRequest: ChannelRequest){
        channelViewModel.handleRequest(
            request = {repository.postCreateChannel(guildId, channelRequest)},
            successHandler = {it}
        )
    }

    fun loadSessionData(guildId: Long, channelId: Long) {
        sessionViewModel.handleRequest(
            request = { repository.postSpecificSession(guildId, channelId) },
            successHandler = { it },
        )
    }
}
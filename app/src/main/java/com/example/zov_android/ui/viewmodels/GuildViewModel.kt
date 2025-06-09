package com.example.zov_android.ui.viewmodels

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zov_android.data.models.request.ChannelRequest
import com.example.zov_android.data.models.request.GuildRequest
import com.example.zov_android.data.models.request.InvitesRequest
import com.example.zov_android.data.models.response.ChannelResponse
import com.example.zov_android.data.models.response.GuildResponse
import com.example.zov_android.data.models.response.InvitesResponse
import com.example.zov_android.data.models.response.MembersGuildResponse
import com.example.zov_android.data.models.response.MessagesResponse
import com.example.zov_android.data.models.response.SessionResponse
import com.example.zov_android.data.repository.MainRepository
import com.example.zov_android.data.signalr.SignalR
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class GuildViewModel @Inject constructor(
    private val repository: MainRepository
): ViewModel(){

    private val guildViewModel = @SuppressLint("StaticFieldLeak")
    object: BaseViewModel<GuildResponse>(ViewState.Idle) {}

    private val guildMembersViewModel = @SuppressLint("StaticFieldLeak")
    object: BaseViewModel<List<MembersGuildResponse>>(ViewState.Idle) {}

    private val guildListViewModel = @SuppressLint("StaticFieldLeak")
    object : BaseViewModel<List<GuildResponse>>(ViewState.Idle){}

    private val guildInvitesViewModel = @SuppressLint("StaticFieldLeak")
    object : BaseViewModel<InvitesResponse>(ViewState.Idle){}

    private val channelViewModel = @SuppressLint("StaticFieldLeak")
    object: BaseViewModel<ChannelResponse>(ViewState.Idle) {}

    private val channelListViewModel = @SuppressLint("StaticFieldLeak")
    object: BaseViewModel<List<ChannelResponse>>(ViewState.Idle) {}

    private val sessionViewModel = @SuppressLint("StaticFieldLeak")
    object: BaseViewModel<SessionResponse>(ViewState.Idle) {}

    @Inject lateinit var signalR: SignalR

    // Публичные состояния
    val guildState: StateFlow<BaseViewModel.ViewState<GuildResponse>> = guildViewModel.state
    val guildMembersState: StateFlow<BaseViewModel.ViewState<List<MembersGuildResponse>>> = guildMembersViewModel.state
    val listGuildState: StateFlow<BaseViewModel.ViewState<List<GuildResponse>>> = guildListViewModel.state

    val guildInvitesState: StateFlow<BaseViewModel.ViewState<InvitesResponse>> = guildInvitesViewModel.state


    val channelState: StateFlow<BaseViewModel.ViewState<ChannelResponse>> = channelViewModel.state
    val listChannelState: StateFlow<BaseViewModel.ViewState<List<ChannelResponse>>> = channelListViewModel.state

    val sessionState: StateFlow<BaseViewModel.ViewState<SessionResponse>> = sessionViewModel.state

    fun addNewChannel(channel: ChannelResponse) {
        val currentState = channelListViewModel.state.value
        if (currentState is BaseViewModel.ViewState.Success && currentState.data.isNotEmpty()) {
            val newList = currentState.data.toMutableList().apply {
                add(channel)
            }
            channelListViewModel._state.value = BaseViewModel.ViewState.Success(newList)
        }
    }

    fun loadGuildData(token:String, guildRequest: GuildRequest){
        guildViewModel.handleRequest(
            request = {repository.postCreateGuild(token, guildRequest)},
            successHandler = { it }
        )
    }

    fun getGuildsUser(token:String){
        guildListViewModel.handleRequest(
            request = {repository.getGuilds(token)},
            successHandler = {it}
        )
    }

    fun getMembersGuild(token: String, guildId: Long){
        guildMembersViewModel.handleRequest(
            request = {repository.getUsersGuild(token, guildId)},
            successHandler = {it}
        )
    }

    fun createInvitation(token: String, guildId: Long, invitesRequest: InvitesRequest){
        guildInvitesViewModel.handleRequest(
            request = {repository.postInvites(token, guildId, invitesRequest)},
            successHandler = {it}
        )
    }

    fun loadChannelData(token:String, guildId: Long, channelRequest: ChannelRequest){
        channelViewModel.handleRequest(
            request = {repository.postCreateChannel(token, guildId, channelRequest)},
            successHandler = {it}
        )
    }

    fun getChannelList(token: String, guildId: Long){
        channelListViewModel.handleRequest(
            request = {repository.getChannelGuild(token, guildId)},
            successHandler = {it}
        )
    }

    fun loadSessionData(token: String, channelId: Long) {
        sessionViewModel.handleRequest(
            request = { repository.postSpecificSession(token, channelId) },
            successHandler = { it },
        )
    }
}
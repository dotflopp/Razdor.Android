package com.example.zov_android.data.repository


import android.content.Context
import com.example.zov_android.data.api.ApiClient
import com.example.zov_android.data.models.request.ChannelRequest
import com.example.zov_android.data.models.request.GuildRequest
import com.example.zov_android.data.models.request.InvitesRequest
import com.example.zov_android.data.models.request.LoginRequest
import com.example.zov_android.data.models.request.SignupRequest
import com.example.zov_android.data.models.request.StatusRequest
import com.example.zov_android.data.models.response.AuthResponse
import com.example.zov_android.data.models.response.ChannelResponse
import com.example.zov_android.data.models.response.ExceptionResponse
import com.example.zov_android.data.models.response.GuildResponse
import com.example.zov_android.data.models.response.InvitesResponse
import com.example.zov_android.data.models.response.MembersGuildResponse
import com.example.zov_android.data.models.response.MessagesResponse
import com.example.zov_android.data.models.response.SessionResponse
import com.example.zov_android.data.models.response.UserResponse
import com.example.zov_android.data.utils.TokenData
import com.example.zov_android.data.utils.decodeToken
import com.example.zov_android.data.utils.parseSnowflake
import com.example.zov_android.domain.utils.DataModel
import com.example.zov_android.data.webrtc.WebRtcManager
import com.example.zov_android.domain.service.MainService
import com.example.zov_android.domain.utils.UserCommunicationSelectedStatus
import org.webrtc.SurfaceViewRenderer
import java.io.File
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepository @Inject constructor(
    private val apiClient: ApiClient,
    private val clientWebRTC: WebRtcManager,
)  {

    private var target: String? = null
    var listener: Listener? = null
    private var remoteView:SurfaceViewRenderer? = null

    suspend fun login(loginRequest: LoginRequest): ApiClient.Result<AuthResponse> {
        return apiClient.login(loginRequest)
    }

    suspend fun signup(signupRequest: SignupRequest): ApiClient.Result<AuthResponse> {
        return apiClient.signUp(signupRequest)
    }

    suspend fun putChangeUserStatus(token: String, status: StatusRequest): ApiClient.Result<Unit>{
        return apiClient.putChangeUserStatus(token, status)
    }

    suspend fun getYourself(token: String): ApiClient.Result<UserResponse> {
        return apiClient.getYourself(token)
    }

    suspend fun postSpecificSession(token: String, channelId: Long): ApiClient.Result<SessionResponse>{
        return apiClient.postConnectionVoiceChannel(token, channelId)
    }

    suspend fun postCreateGuild(token:String, guildRequest: GuildRequest): ApiClient.Result<GuildResponse>{
        return apiClient.postGuild(token, guildRequest)
    }

    suspend fun postCreateChannel(token:String, guildId: Long, channelRequest: ChannelRequest): ApiClient.Result<ChannelResponse>{
        return apiClient.createChannel(token, guildId, channelRequest)
    }

    suspend fun getChannelGuild(token:String, guildId: Long): ApiClient.Result<List<ChannelResponse>>{
        return apiClient.getChannel(token, guildId)
    }


    suspend fun getGuilds(token: String): ApiClient.Result<List<GuildResponse>>{
        return apiClient.receiveGuilds(token)
    }

    suspend fun getUsersGuild(token: String, guildId: Long): ApiClient.Result<List<MembersGuildResponse>>{
        return apiClient.getUsersGuild(token, guildId)
    }

    suspend fun postInvites(token:String, guildId: Long, invitesRequest: InvitesRequest): ApiClient.Result<InvitesResponse>{
        return apiClient.postInvites(token, guildId, invitesRequest)
    }

    suspend fun createMessages(token: String, context:Context, channelId: Long, text:String, files:List<File>?):ApiClient.Result<MessagesResponse>{
        return apiClient.createMessages(token, context, channelId, text, files)
    }

    suspend fun claimMessages(token: String, channelId: Long):ApiClient.Result<List<MessagesResponse>>{
        return apiClient.claimMessages(token, channelId)
    }

    suspend fun claimAttachment(token: String, channelId: Long, messageId:Long, attachmentId:Long):ApiClient.Result<File>{
        return apiClient.claimAttachment(token, channelId, messageId, attachmentId)
    }

    fun decodingToken(token: String): TokenData {
        return decodeToken(token)
    }
    fun parseSnowflakeMr(token: Long): Triple<Long, Int, Int> {
        return parseSnowflake(token)
    }


    suspend fun getSpecificUser(userId: Long): ApiClient.Result<UserResponse>{
        return  apiClient.getSpecificUser(userId)
    }


    fun setTarget(target: String) {
        this.target = target
    }


    fun initWebRtcClient(username: String){
        clientWebRTC.initializeWebrtcClient(username)
    }



    fun initLocalSurfaceView(view: SurfaceViewRenderer, isVideoCall: Boolean) {
        //if (MainService.isLocalViewInitialized) return
        clientWebRTC.initLocalSurfaceView(view, isVideoCall)
    }

    fun initRemoteSurfaceView(view: SurfaceViewRenderer) {
        //if (MainService.isRemoteViewInitialized) return
        clientWebRTC.initRemoteSurfaceView(view)
        this.remoteView = view
    }

    fun sendEndCall() {
        /*onTransferEventToSocket(
            DataModel(
                type = DataModelType.EndCall,
                target = target!!
            )
        )*/
    }

    // авто-отключение звука
    fun toggleAudio(shouldBeMuted: Boolean) {
        clientWebRTC.toggleAudio(shouldBeMuted)
    }

    // авто-отключение видео
    fun toggleVideo(shouldBeMuted: Boolean) {
        clientWebRTC.toggleVideo(shouldBeMuted)
    }

    fun switchCamera() {
        clientWebRTC.switchCamera()
    }

    // передача ласт ивента другому участику\

    interface Listener{
        // функция получения ласт ивента
        fun onLatestEventReceived(dataModel: DataModel)

        fun endCall()
    }

    fun startCall() {
        //clientWebRTC.startCall("Lukus")
    }



}
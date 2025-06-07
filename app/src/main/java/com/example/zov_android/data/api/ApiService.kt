package com.example.zov_android.data.api


import com.example.zov_android.data.models.request.ChannelRequest
import com.example.zov_android.data.models.request.GuildRequest
import com.example.zov_android.data.models.request.InvitesRequest
import com.example.zov_android.data.models.request.SignupRequest
import com.example.zov_android.data.models.response.AuthResponse
import com.example.zov_android.data.models.request.LoginRequest
import com.example.zov_android.data.models.request.StatusRequest
import com.example.zov_android.data.models.response.ChannelResponse
import com.example.zov_android.data.models.response.GuildResponse
import com.example.zov_android.data.models.response.InvitesResponse
import com.example.zov_android.data.models.response.MembersGuildResponse
import com.example.zov_android.data.models.response.MessagesResponse
import com.example.zov_android.data.models.response.SessionResponse
import com.example.zov_android.data.models.response.UserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path

interface ApiService {

    @POST("auth/login")
    suspend fun postLogin(@Body params: LoginRequest): Response<AuthResponse>

    @POST("auth/signup")
    suspend fun postSignUp(@Body params: SignupRequest): Response<AuthResponse>


    @GET("users/@me")
    suspend fun getMeUser(@Header("Authorization") token: String): Response<UserResponse>

    @PUT("users/@me/status")
    suspend fun putSelectedStatus(
        @Header("Authorization") token: String,
        @Body params: StatusRequest
    ): Response<Unit>

    @GET("users/{userID}")
    suspend fun getIdUser(@Path("userId") userId: Long): Response<UserResponse>

    @POST("communities")
    suspend fun postGuilds(
        @Header("Authorization") token: String,
        @Body params: GuildRequest
    ): Response<GuildResponse>

    @GET("communities/@my")
    suspend fun getMyGuilds(@Header("Authorization") token: String): Response<List<GuildResponse>>

    @GET("communities/{communityId}/members")
    suspend fun getMembersGuild(
        @Header("Authorization") token: String,
        @Path("communityId") guildId: Long,
    ): Response<List<MembersGuildResponse>>

    @POST("communities/{communityId}/invites")
    suspend fun postInvitation(
        @Header("Authorization") token: String,
        @Path("communityId") guildId: Long,
        @Body params: InvitesRequest
    ): Response<InvitesResponse>

    @POST("communities/{communityId}/channels")
    suspend fun postChannels(
        @Header("Authorization") token: String,
        @Path("communityId") guildId: Long,
        @Body params: ChannelRequest
    ):Response<ChannelResponse>

    @GET("communities/{communityId}/channels")
    suspend fun getChannels(
        @Header("Authorization") token: String,
        @Path("communityId") guildId: Long
    ):Response<List<ChannelResponse>>

    @POST("channels/{channelId}/connect")
    suspend fun postConnect(
        @Header("Authorization") token: String,
        @Path("channelId") channelId: Long
    ): Response<SessionResponse>

    @Multipart
    @POST("channels/{channelId}/messages")
    suspend fun postMessages(
        @Header("Authorization") token: String,
        @Path("channelId") channelId: Long,
        @Part jsonPart: MultipartBody.Part,
        @Part filesParts: List<MultipartBody.Part>
    ): Response<MessagesResponse>

    @GET("channels/{channelId}/messages")
    suspend fun getMessages(
        @Header("Authorization") token: String,
        @Path("channelId") channelId: Long
    ): Response<List<MessagesResponse>>

    @GET("attachments/{channelId}/{messageId}/{attachmentId}")
    suspend fun getAttachment(
        @Header("Authorization") token: String,
        @Path("channelId") channelId: Long,
        @Path("messageId") messageId: Long,
        @Path("attachmentId") attachmentId: Long
    ): Response<Unit>
}
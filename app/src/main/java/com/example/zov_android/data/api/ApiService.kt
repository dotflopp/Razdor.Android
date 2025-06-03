package com.example.zov_android.data.api


import com.example.zov_android.data.models.request.ChannelRequest
import com.example.zov_android.data.models.request.GuildRequest
import com.example.zov_android.data.models.request.SignupRequest
import com.example.zov_android.data.models.response.AuthResponse
import com.example.zov_android.data.models.request.LoginRequest
import com.example.zov_android.data.models.request.StatusRequest
import com.example.zov_android.data.models.response.ChannelResponse
import com.example.zov_android.data.models.response.GuildResponse
import com.example.zov_android.data.models.response.SessionResponse
import com.example.zov_android.data.models.response.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @GET("communities/@my")
    suspend fun getMyGuilds(@Header("Authorization") token: String): Response<List<GuildResponse>>

    @POST("communities")
    suspend fun postGuilds(
        @Header("Authorization") token: String,
        @Body params: GuildRequest
    ): Response<GuildResponse>

    @GET("users/@me")
    suspend fun getMeUser(@Header("Authorization") token: String): Response<UserResponse>

    @PUT("users/@me/status")
    suspend fun putSelectedStatus(
        @Header("Authorization") token: String,
        @Body params: StatusRequest
    ): Response<Unit>

    @GET("users/{userID}")
    suspend fun getIdUser(@Path("userId") userId: Long): Response<UserResponse>

    @POST("auth/login")
    suspend fun postLogin(@Body params: LoginRequest): Response<AuthResponse>

    @POST("auth/signup")
    suspend fun postSignUp(@Body params: SignupRequest): Response<AuthResponse>


    @POST("communities/{communityId}/channels")
    suspend fun postChannels(
        @Path("guildId") guildId: Long,
        @Body params: ChannelRequest
    ):Response<ChannelResponse>

    @POST("communities/{communityId}/channels/{channelId}/join")
    suspend fun postSessionId(
        @Path("guildId") guildId: Long,
        @Path("channelId") channelId: Long
    ): Response<SessionResponse>
}
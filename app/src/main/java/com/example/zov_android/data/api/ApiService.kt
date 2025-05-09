package com.example.zov_android.data.api


import com.example.zov_android.data.models.Guild
import com.example.zov_android.data.models.request.SignupRequest
import com.example.zov_android.data.models.response.AuthResponse
import com.example.zov_android.data.models.request.UserRequest
import com.example.zov_android.data.models.request.LoginRequest
import com.example.zov_android.data.models.response.UserResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @GET("guilds/@my")
    suspend fun getMyGuilds(): Response<List<Guild>>

    @GET("users/@me")
    suspend fun getMeUser(@Header("Authorization") token: String): Response<UserResponse>

    @GET("users/{userID}")
    suspend fun getIdUser(@Path("userId") userId: Long): Response<UserResponse>

    @POST("auth/login")
    suspend fun postLogin(@Body params: LoginRequest): Response<AuthResponse>

    @POST("auth/signup")
    suspend fun postSignUp(@Body params: SignupRequest): Response<AuthResponse>

}
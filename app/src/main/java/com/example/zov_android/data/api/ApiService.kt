package com.example.zov_android.data.api


import com.example.zov_android.data.models.Guild
import com.example.zov_android.data.models.request.SignupRequest
import com.example.zov_android.data.models.response.AuthResponse
import com.example.zov_android.data.models.request.UserRequest
import com.example.zov_android.data.models.request.LoginRequest
import com.example.zov_android.data.models.response.UserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @GET("guilds/@my")
    fun getMyGuilds(): Call<List<Guild>>

    @GET("users/@me")
    fun getMeUser(@Header("Authorization") token: String): Call<UserResponse>

    @GET("users/{userID}")
    fun getIdUser(@Path("userId") userId: Long): Call<UserResponse>

    @POST("auth/login")
    fun postLogin(@Body params: LoginRequest): Call<AuthResponse>

    @POST("auth/signup")
    fun postSignUp(@Body params: SignupRequest): Call<AuthResponse>

}
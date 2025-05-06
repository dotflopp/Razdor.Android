package com.example.zov_android.data.api


import com.example.zov_android.data.models.Guild
import com.example.zov_android.data.models.request.SignupRequest
import com.example.zov_android.data.models.response.AuthResponse
import com.example.zov_android.data.models.User
import com.example.zov_android.data.models.request.LoginRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @GET("guilds/@my")
    fun getMyGuilds(): Call<List<Guild>>

    @GET("users/@me")
    fun getMeUser(): Call<User>

    @GET("users/{userID}")
    fun getIdUser(): Call<User>

    @POST("auth/login")
    fun postLogin(@Body params: LoginRequest): Call<AuthResponse>

    @POST("auth/signup")
    fun postSignUp(@Body params: SignupRequest): Call<AuthResponse>

}
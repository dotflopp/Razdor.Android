package com.example.zov_android.data.api

import retrofit2.Call
import retrofit2.http.GET
interface ApiService {
    @GET("guilds/@my")
    fun getMyGuilds(): Call<List<Guild>>
}
data class Guild(
    val name: String,
)
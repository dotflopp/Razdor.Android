package com.example.zov_android.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//http://26.101.132.34:5154/
//https://dotflopp.ru/api/
object RetrofitClient {
    private const val BASE_URL = "https://dotflopp.ru/api/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
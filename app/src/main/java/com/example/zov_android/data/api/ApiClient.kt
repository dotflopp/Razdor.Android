package com.example.zov_android.data.api

import com.example.zov_android.data.models.response.ExceptionResponse
import com.example.zov_android.data.models.Guild
import com.example.zov_android.data.models.request.LoginRequest
import com.example.zov_android.data.models.request.SignupRequest
import com.example.zov_android.data.models.response.AuthResponse
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class ApiClient @Inject constructor(
    private val apiService: ApiService
) {
    fun fetchGuilds(callback: (Boolean, String?, List<Guild>?) -> Unit){
        apiService.getMyGuilds().enqueue(object : Callback<List<Guild>> {
            override fun onResponse(call: Call<List<Guild>>, response: Response<List<Guild>>) {
                if (response.isSuccessful) {
                    val guilds = response.body()
                    callback(true, null, guilds)
                } else {
                    callback(false, "Server error: ${response.code()}", null)
                }
            }

            override fun onFailure(call: Call<List<Guild>>, t: Throwable) {
                callback(false, "Request failed: ${t.message}", null)
            }
        })
    }

    fun postSignUp(signupRequest: SignupRequest, callback: (Boolean, String?, AuthResponse?)->Unit){
        apiService.postSignUp(signupRequest).enqueue(object: Callback<AuthResponse>{
            override fun onResponse(
                call: Call<AuthResponse>,
                response: Response<AuthResponse>
            ) {
                when {
                    response.isSuccessful -> {
                        val signupResponse = response.body()
                        callback(true, null, signupResponse)
                    }

                    response.code() in 400..499 -> {
                        val errorBody = response.errorBody()?.string()
                        try {
                            val exceptionResponse = Gson().fromJson(errorBody, ExceptionResponse::class.java)
                            callback(false, "Client error: ${exceptionResponse.message}", null)
                        } catch (e: Exception) {
                            callback(false, "Client error: Не удалось распарсить ответ", null)
                        }
                    }

                    else -> {
                        callback(false, "Server error: ${response.code()}", null)
                    }
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                callback(false, "Request failed: ${t.message}", null)
            }
        })
    }

    fun postLogin(loginRequest: LoginRequest, callback: (Boolean, String?, AuthResponse?) -> Unit){
        apiService.postLogin(loginRequest).enqueue(object: Callback<AuthResponse> {
            override fun onResponse(
                call: Call<AuthResponse>,
                response: Response<AuthResponse>
            ) {
                when {
                    response.isSuccessful -> {
                        val signupResponse = response.body()
                        callback(true, null, signupResponse)
                    }

                    response.code() in 400..499 -> {
                        val errorBody = response.errorBody()?.string()
                        try {
                            val exceptionResponse = Gson().fromJson(errorBody, ExceptionResponse::class.java)
                            callback(false, "Client error: ${exceptionResponse.message}", null)
                        } catch (e: Exception) {
                            callback(false, "Client error: Не удалось распарсить ответ", null)
                        }
                    }

                    else -> {
                        callback(false, "Server error: ${response.code()}", null)
                    }
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                callback(false, "Request failed: ${t.message}", null)
            }

        })
    }

}
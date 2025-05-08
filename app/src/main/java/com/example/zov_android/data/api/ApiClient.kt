package com.example.zov_android.data.api

import android.util.Log
import com.example.zov_android.data.models.response.ExceptionResponse
import com.example.zov_android.data.models.Guild
import com.example.zov_android.data.models.request.LoginRequest
import com.example.zov_android.data.models.request.SignupRequest
import com.example.zov_android.data.models.response.AuthResponse
import com.example.zov_android.data.models.response.UserResponse
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class ApiClient @Inject constructor(
    private val apiService: ApiService
) {
    private inline fun <T> handleCall(
        call: Call<T>,
        crossinline callback: (Boolean, String?, T?) -> Unit
    ) {
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                when {
                    response.isSuccessful -> {
                        callback(true, null, response.body())
                    }
                    response.code() in 400..499 -> handleClientError(response, callback)
                    else -> callback(false, "Server error: ${response.code()}", null)
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                callback(false, "Request failed: ${t.message}", null)
            }
        })
    }

    private inline fun <T> handleClientError(
        response: Response<T>,
        crossinline callback: (Boolean, String?, T?) -> Unit
    ) {
        try {
            Log.d("API_ERROR", "Response code: ${response.code()}")

            val errorBody = response.errorBody()?.string()
            Log.d("API_ERROR", "Error body: $errorBody")

            when {
                errorBody.isNullOrBlank() -> callback(false, "Client error: Пустой ответ", null)
                else -> try {
                    val exceptionResponse = Gson().fromJson(errorBody, ExceptionResponse::class.java)
                    callback(false, "Client error: ${exceptionResponse.message}", null)
                } catch (e: Exception) {
                    Log.e("API_ERROR", "Ошибка парсинга", e)
                    callback(false, "Client error: Не удалось распарсить ответ", null)
                }
            }
        } catch (e: Exception) {
            Log.e("API_ERROR", "Обработка ошибок", e)
            callback(false, "Client error: ${e.message ?: "Неизвестная ошибка"}", null)
        }
    }

    fun fetchGuilds(callback: (Boolean, String?, List<Guild>?) -> Unit) {
        handleCall(apiService.getMyGuilds(), callback)
    }

    fun getSpecificUser(userId: Long, callback: (Boolean, String?, UserResponse?) -> Unit){
        handleCall(apiService.getIdUser(userId), callback)
    }

    fun getYourself(token: String, callback: (Boolean, String?, UserResponse?) -> Unit) {
        handleCall(apiService.getMeUser("Bearer $token"), callback)
    }

    fun signUp(signupRequest: SignupRequest, callback: (Boolean, String?, AuthResponse?) -> Unit) {
        handleCall(apiService.postSignUp(signupRequest), callback)
    }

    fun login(loginRequest: LoginRequest, callback: (Boolean, String?, AuthResponse?) -> Unit) {
        handleCall(apiService.postLogin(loginRequest), callback)
    }
}


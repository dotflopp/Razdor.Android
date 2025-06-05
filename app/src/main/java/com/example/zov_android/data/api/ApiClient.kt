package com.example.zov_android.data.api

import com.example.zov_android.data.models.request.ChannelRequest
import com.example.zov_android.data.models.response.ExceptionResponse
import com.example.zov_android.data.models.request.GuildRequest
import com.example.zov_android.data.models.request.InvitesRequest
import com.example.zov_android.data.models.request.LoginRequest
import com.example.zov_android.data.models.request.SignupRequest
import com.example.zov_android.data.models.request.StatusRequest
import com.example.zov_android.data.models.response.AuthResponse
import com.example.zov_android.data.models.response.ChannelResponse
import com.example.zov_android.data.models.response.GuildResponse
import com.example.zov_android.data.models.response.InvitesResponse
import com.example.zov_android.data.models.response.MembersGuildResponse
import com.example.zov_android.data.models.response.SessionResponse
import com.example.zov_android.data.models.response.UserResponse
import com.example.zov_android.domain.utils.UserCommunicationSelectedStatus
import com.google.gson.Gson
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiClient @Inject constructor(
    private val apiService: ApiService,
    private val gson: Gson
) {
    sealed class Result<out T> {
        data class Success<out T>(val data: T) : Result<T>()
        data class Error(
            val type: ErrorType,
            val statusCode: Int? = null,
            val message: String? = null,
            val responseBody: String? = null
        ) : Result<Nothing>()
    }

    enum class ErrorType {
        NETWORK,
        CLIENT,
        SERVER,
        PARSING,
        UNKNOWN
    }

    private suspend fun <T> safeApiCall(
        call: suspend () -> Response<T>
    ): Result<T> {
        return try {
            val response = call()
            handleResponse(response)
        } catch (e: Exception) {
            handleException(e)
        }
    }

    private fun <T> handleResponse(response: Response<T>): Result<T> {
        return when {
            response.isSuccessful -> {
                val body = response.body()
                if (body != null) {
                    Result.Success(body)
                } else {
                    Result.Error(
                        type = ErrorType.PARSING,
                        message = "Пустое тело"
                    )
                }
            }
            response.code() in 400..499 -> handleClientError(response)
            else -> Result.Error(
                type = ErrorType.SERVER,
                statusCode = response.code(),
                message = "Server error"
            )
        }
    }

    private fun <T> handleClientError(response: Response<T>): Result.Error {
        return try {
            val errorBody = response.errorBody()?.string().orEmpty()
            val exceptionResponse = try {
                gson.fromJson(errorBody, ExceptionResponse::class.java)
            } catch (e: Exception) {
                null
            }

            Result.Error(
                type = ErrorType.CLIENT,
                statusCode = response.code(),
                message = exceptionResponse?.message ?: "Client error",
                responseBody = errorBody
            )
        } catch (e: Exception) {
            Result.Error(
                type = ErrorType.PARSING,
                message = "Ошибка парсинга. Client error: ${e.message}"
            )
        }
    }

    private fun handleException(e: Exception): Result.Error {
        return when (e) {
            is IOException -> Result.Error(
                type = ErrorType.NETWORK,
                message = "Network error: ${e.message}"
            )
            else -> Result.Error(
                type = ErrorType.UNKNOWN,
                message = "Unknown error: ${e.message}"
            )
        }
    }

    suspend fun postGuild(token:String, guildRequest: GuildRequest):Result<GuildResponse> = safeApiCall {
        apiService.postGuilds("Bearer $token", guildRequest)
    }

    suspend fun receiveGuilds(token: String): Result<List<GuildResponse>> = safeApiCall {
        apiService.getMyGuilds("Bearer $token")
    }

    suspend fun getUsersGuild(token: String, guildId: Long): Result<List<MembersGuildResponse>> = safeApiCall{
        apiService.getMembersGuild(token, guildId)
    }

    suspend fun postInvites(token: String, guildId: Long, invitesRequest: InvitesRequest): Result<InvitesResponse> = safeApiCall{
        apiService.postInvitation("Bearer $token", guildId, invitesRequest)
    }


    suspend fun createChannel(token: String, guildId:Long, channelRequest: ChannelRequest): Result<ChannelResponse> = safeApiCall {
        apiService.postChannels("Bearer $token", guildId, channelRequest)
    }

    suspend fun getChannel(token: String, guildId: Long): Result<List<ChannelResponse>> = safeApiCall{
        apiService.getChannels("Bearer $token", guildId)
    }

    suspend fun postConnectionVoiceChannel(token: String, channelId:Long): Result<SessionResponse> = safeApiCall{
        apiService.postConnect("Bearer $token", channelId)
    }

    suspend fun getSpecificUser(userId: Long): Result<UserResponse> = safeApiCall {
        apiService.getIdUser(userId)
    }

    suspend fun putChangeUserStatus(token: String, status: StatusRequest): Result<Unit> = safeApiCall {
        apiService.putSelectedStatus("Bearer $token", status)
    }

    suspend fun getYourself(token: String): Result<UserResponse> = safeApiCall {
        apiService.getMeUser("Bearer $token")
    }

    suspend fun signUp(signupRequest: SignupRequest): Result<AuthResponse> = safeApiCall {
        apiService.postSignUp(signupRequest)
    }

    suspend fun login(loginRequest: LoginRequest): Result<AuthResponse> = safeApiCall {
        apiService.postLogin(loginRequest)
    }
}


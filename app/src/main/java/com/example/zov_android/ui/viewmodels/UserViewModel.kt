package com.example.zov_android.ui.viewmodels

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import com.example.zov_android.data.models.request.StatusRequest
import com.example.zov_android.data.models.response.ExceptionResponse
import com.example.zov_android.data.models.response.UserResponse
import com.example.zov_android.data.repository.MainRepository
import com.example.zov_android.domain.utils.UserCommunicationSelectedStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private val userViewModel = @SuppressLint("StaticFieldLeak")
    object: BaseViewModel<UserResponse>(ViewState.Idle){}

    private val statusViewModel = @SuppressLint("StaticFieldLeak")
    object: BaseViewModel<Unit>(ViewState.Idle){}

    val userState: StateFlow<BaseViewModel.ViewState<UserResponse>> = userViewModel.state
    val statusState: StateFlow<BaseViewModel.ViewState<Unit>> = statusViewModel.state

    fun loadUserData(token: String?) {
        userViewModel.handleRequest(
            request = { repository.getYourself(token!!) },
            successHandler = { it }
        )
    }

    fun loadUserSelectedStatus(token: String, status: StatusRequest){
        statusViewModel.handleRequest(
            request = {repository.putChangeUserStatus(token, status)},
            successHandler = {
                loadUserData(token)
            }
        )
    }
}
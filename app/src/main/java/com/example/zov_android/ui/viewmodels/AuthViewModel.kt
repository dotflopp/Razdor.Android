package com.example.zov_android.ui.viewmodels


import androidx.lifecycle.viewModelScope
import com.example.zov_android.data.api.ApiClient
import com.example.zov_android.data.models.request.LoginRequest
import com.example.zov_android.data.models.request.SignupRequest
import com.example.zov_android.data.models.response.AuthResponse
import com.example.zov_android.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: MainRepository
) : BaseViewModel<AuthResponse>(BaseViewModel.ViewState.Idle) {

    fun loadLoginData(loginRequest: LoginRequest) {
        handleRequest(
            request = { repository.login(loginRequest) }
        ) { it }
    }

    fun loadSignupData(signupRequest: SignupRequest) {
        handleRequest(
            request = { repository.signup(signupRequest) }
        ) { it }
    }
}
package com.example.zov_android.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zov_android.data.api.ApiClient
import com.example.zov_android.data.models.response.UserResponse
import com.example.zov_android.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: MainRepository
) : BaseViewModel<UserResponse>(BaseViewModel.ViewState.Idle) {

    fun loadUserData(token: String?) {
        if (token.isNullOrEmpty()) {
            _state.value = BaseViewModel.ViewState.Error("Токен отсутствует")
            return
        }

        handleRequest(
            request = { repository.getYourself(token) },
            successHandler = { it }
        )
    }
}
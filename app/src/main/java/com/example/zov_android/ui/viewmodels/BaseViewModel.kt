package com.example.zov_android.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zov_android.data.api.ApiClient
import com.example.zov_android.data.models.response.GuildResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


abstract class BaseViewModel<T> (initialState: ViewState<T>) : ViewModel() {
    val _state = MutableStateFlow<ViewState<T>>(initialState)
    val state: StateFlow<ViewState<T>> = _state

    fun <R> handleRequest(
        request: suspend () -> ApiClient.Result<R>,
        successHandler: (R) -> T
    ) {
        _state.value = ViewState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                when (val result = request()) {
                    is ApiClient.Result.Success -> {
                        val data = result.data?.let(successHandler)
                        data?.let {
                            Log.d("BaseVM", "result:$it")
                            _state.value = ViewState.Success(it)
                        } ?: run {
                            Log.e("BaseVM", "Unexpected data type: ${result.data}")
                            _state.value = ViewState.Error("Invalid response format")
                        }
                    }
                    is ApiClient.Result.Error -> handleApiError(result)
                }
            } catch (e: Exception) {
                _state.value = ViewState.Error("Unexpected error: ${e.message}")
                Log.e("BaseVM", "Exception: ${e.stackTraceToString()}")
            }
        }
    }

    private fun handleApiError(error: ApiClient.Result.Error) {
        val errorMsg = when (error.type) {
            ApiClient.ErrorType.CLIENT -> "Client error: ${error.message}. Status code: ${error.statusCode}. Response: ${error.responseBody}"
            ApiClient.ErrorType.NETWORK -> "Network error: ${error.message}"
            ApiClient.ErrorType.PARSING -> "Parsing error: ${error.message}"
            ApiClient.ErrorType.SERVER -> "Server error: ${error.message}. Status code: ${error.statusCode}. Response: ${error.responseBody}"
            ApiClient.ErrorType.UNKNOWN -> "Unknown error: ${error.message}"
        }
        _state.value = ViewState.Error(errorMsg)
        Log.e("BaseVM", errorMsg)
    }

    sealed class ViewState<out T> {
        data object Idle : ViewState<Nothing>()
        data object Loading : ViewState<Nothing>()
        data class Success<out T>(val data: T) : ViewState<T>()
        data class Error(val message: String) : ViewState<Nothing>()
    }
}
package com.example.zov_android.ui.fragments.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.zov_android.data.models.request.LoginRequest
import com.example.zov_android.data.models.response.AuthResponse
import com.example.zov_android.databinding.FragmentLoginBinding
import com.example.zov_android.data.repository.MainRepository
import com.example.zov_android.ui.activities.MainActivity
import com.example.zov_android.ui.fragments.navigation.NavigableFragment
import com.example.zov_android.ui.fragments.navigation.NavigationInsideFragment
import com.example.zov_android.ui.viewmodels.AuthViewModel
import com.example.zov_android.ui.viewmodels.BaseViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : NavigableFragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()


    override fun onCreateView(context: Context): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        binding.apply {

            btn.setOnClickListener {
                val email = emailEt.text.toString()
                val password = passwordEt.text.toString()

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(requireContext(), "Пожалуйста, введите имя пользователя и пароль", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                authViewModel.loadLoginData(LoginRequest(email, password))
                lifecycleScope.launch {
                    authViewModel.state.collect(){state->
                        when(state){
                            is BaseViewModel.ViewState.Error -> handleError(state.message)
                            BaseViewModel.ViewState.Idle -> {}
                            BaseViewModel.ViewState.Loading -> {}
                            is BaseViewModel.ViewState.Success -> handleSuccess(state.data)
                        }
                    }
                }
            }

            TransitionBtn.setOnClickListener {
                navigation.push(RegFragment())
            }
        }
    }

    private fun handleSuccess(authResponse: AuthResponse) {
        val intent = Intent(requireContext(), MainActivity::class.java).apply {
            putExtra("userparams", authResponse)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        requireActivity().finish()
    }

    private fun handleError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        Log.e("AuthError", message)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
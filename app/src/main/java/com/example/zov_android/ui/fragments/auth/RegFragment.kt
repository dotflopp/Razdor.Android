package com.example.zov_android.ui.fragments.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.zov_android.data.models.request.SignupRequest
import com.example.zov_android.databinding.FragmentRegBinding
import com.example.zov_android.data.repository.MainRepository
import com.example.zov_android.ui.activities.MainActivity
import com.example.zov_android.ui.fragments.navigation.NavigableFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RegFragment : NavigableFragment() {
    private var _binding: FragmentRegBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var mainRepository: MainRepository

    override fun onCreateView(context: Context): View {
        _binding = FragmentRegBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        binding.apply {
            btn.setOnClickListener {
                val username = usernameEt.text.toString()
                val email = emailEt.text.toString()
                val password = passwordEt.text.toString()

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(requireContext(), "Пожалуйста, введите имя пользователя и пароль", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                mainRepository.signup(
                    signupRequest = SignupRequest(username, email, password))
                    { isSuccessful, errorMessage, result ->
                        if (!isSuccessful) {
                            requireActivity().runOnUiThread {
                                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                            }
                            Log.e("ApiError", errorMessage ?: "Unknown error")
                        } else {
                            Log.d("Navigation", "Starting MainActivity...")
                            requireActivity().runOnUiThread {
                                val intent = Intent(requireContext(), MainActivity::class.java).apply {
                                    putExtra("userparams", result)
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                                startActivity(intent)
                                requireActivity().finish()
                            }
                        }
                }
            }

            TransitionBtn.setOnClickListener {
                navigation.push(LoginFragment())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


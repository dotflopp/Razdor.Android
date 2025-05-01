package com.example.zov_android.ui.fragments.auth

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.zov_android.databinding.FragmentRegBinding
import com.example.zov_android.data.repository.MainRepository
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
                val password = passwordEt.text.toString()

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(requireContext(), "Пожалуйста, введите имя пользователя и пароль", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                mainRepository.reg(username, password) { isDone, message ->
                    if (!isDone) {
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    } else {
                        // Переход на MainActivity (можно заменить на другой фрагмент при необходимости)
                        val intent = requireActivity().intent
                        intent.setClassName(requireContext(), "com.example.zov_android.ui.activities.MainActivity")
                        intent.putExtra("username", username)
                        startActivity(intent)
                    }
                }
            }

            TransitionBtn.setOnClickListener {
                // Переход на LoginFragment
                navigation?.push(LoginFragment())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
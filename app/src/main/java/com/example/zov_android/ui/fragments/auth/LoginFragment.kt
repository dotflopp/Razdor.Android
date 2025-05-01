package com.example.zov_android.ui.fragments.auth

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.zov_android.data.api.Guild
import com.example.zov_android.data.api.RetrofitClient
import com.example.zov_android.databinding.FragmentLoginBinding
import com.example.zov_android.data.repository.MainRepository
import com.example.zov_android.ui.fragments.navigation.NavigableFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : NavigableFragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var mainRepository: MainRepository

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
            btnServ.setOnClickListener {
                RetrofitClient.apiService.getMyGuilds().enqueue(object : Callback<List<Guild>> {
                    override fun onResponse(call: Call<List<Guild>>, response: Response<List<Guild>>) {
                        if (response.isSuccessful) {
                            val guilds = response.body()
                            guilds?.let {
                                val guildNames = it.joinToString(", ") { guild -> guild.name }
                                Toast.makeText(requireContext(), "Guilds: $guildNames", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(requireContext(), "Server error: ${response.code()}", Toast.LENGTH_LONG).show()
                            Log.e("ApiError", "Server error: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<List<Guild>>, t: Throwable) {
                        Toast.makeText(requireContext(), "Request failed: ${t.message}", Toast.LENGTH_LONG).show()
                        Log.e("ApiError", "Request failed: ${t.message}", t)
                    }
                })
            }

            btn.setOnClickListener {
                val username = usernameEt.text.toString()
                val password = passwordEt.text.toString()

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(requireContext(), "Пожалуйста, введите имя пользователя и пароль", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                mainRepository.login(username, password) { isDone, message ->
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
                // Переход на RegFragment
                navigation?.push(RegFragment())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
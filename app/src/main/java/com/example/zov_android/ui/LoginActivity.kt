package com.example.zov_android.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.zov_android.api.Guild
import com.example.zov_android.api.RetrofitClient
import com.example.zov_android.databinding.ActivityLoginBinding
import com.example.zov_android.repository.MainRepository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint //точка входа в приложение
class LoginActivity : AppCompatActivity() {
    private lateinit var views: ActivityLoginBinding
    @Inject lateinit var mainRepository: MainRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        views = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(views.root)
        init()
    }

    private fun init(){
        views.apply {

            btnServ.setOnClickListener {
                RetrofitClient.apiService.getMyGuilds().enqueue(object : Callback<List<Guild>> {
                    override fun onResponse(call: Call<List<Guild>>, response: Response<List<Guild>>) {
                        if (response.isSuccessful) {
                            val guilds = response.body()
                            guilds?.let {
                                val guildNames = it.joinToString(", ") { guild -> guild.name }
                                Toast.makeText(this@LoginActivity, "Guilds: $guildNames", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(this@LoginActivity, "Server error: ${response.code()}", Toast.LENGTH_LONG).show()
                            Log.e("ApiError", "Server error: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<List<Guild>>, t: Throwable) {
                        Toast.makeText(this@LoginActivity, "Request failed: ${t.message}", Toast.LENGTH_LONG).show()
                        Log.e("ApiError", "Request failed: ${t.message}", t)
                    }
                })
            }

            btn.setOnClickListener {
                val username = usernameEt.text.toString()
                val password = passwordEt.text.toString()

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this@LoginActivity, "Пожалуйста, введите имя пользователя и пароль", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                mainRepository.login(username, password) { isDone, message ->
                    if (!isDone) {
                        // выводим причину ошибки
                        Toast.makeText(this@LoginActivity, message , Toast.LENGTH_SHORT).show()
                    } else { // успешный вход в систему
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java).apply {
                            putExtra("username", username)
                        })
                        //finish()
                    }
                }
            }
            TransitionBtn.setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegActivity::class.java))
            }
        }
    }
}
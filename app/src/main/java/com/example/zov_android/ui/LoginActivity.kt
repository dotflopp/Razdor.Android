package com.example.zov_android.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.zov_android.databinding.ActivityLoginBinding
import com.example.zov_android.repository.MainRepository
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
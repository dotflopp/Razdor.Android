package com.example.zov_android.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.zov_android.databinding.ActivityRegBinding
import com.example.zov_android.repository.MainRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RegActivity: AppCompatActivity() {
    private lateinit var views: ActivityRegBinding
    @Inject lateinit var mainRepository: MainRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        views = ActivityRegBinding.inflate(layoutInflater)
        setContentView(views.root)
        init()
    }

    private fun init(){
        views.apply {
            btn.setOnClickListener{
                val username = usernameEt.text.toString()
                val password = passwordEt.text.toString()

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this@RegActivity, "Пожалуйста, введите имя пользователя и пароль", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                mainRepository.reg(username,password){ isDone, message  ->
                    if (!isDone) {
                        // выводим причину ошибки
                        Toast.makeText(this@RegActivity, message , Toast.LENGTH_SHORT).show()
                    } else { // успешный вход в систему
                        startActivity(Intent(this@RegActivity, MainActivity::class.java).apply {
                            putExtra("username", username)
                        })
                    }
                }
            }
            TransitionBtn.setOnClickListener {
                startActivity(Intent(this@RegActivity, LoginActivity::class.java))
            }
        }
    }
}
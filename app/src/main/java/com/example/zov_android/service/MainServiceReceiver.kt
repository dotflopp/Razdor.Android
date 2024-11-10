package com.example.zov_android.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.example.zov_android.repository.MainServiceRepository
import com.example.zov_android.ui.CloseActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainServiceReceiver: BroadcastReceiver() {
    @Inject lateinit var mainServiceRepository: MainServiceRepository

    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent?.action == "ACTION_EXIT"){
            //выход из приложения
            mainServiceRepository.stopService()
            context?.startActivity(Intent(context,CloseActivity::class.java))
        }
    }
}
package com.example.zov_android.ui.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.zov_android.R
import com.example.zov_android.data.models.request.StatusRequest
import com.example.zov_android.data.models.response.AuthResponse
import com.example.zov_android.domain.utils.getCameraAndMicPermission
import com.example.zov_android.data.repository.MainRepository
import com.example.zov_android.data.repository.MainServiceRepository
import com.example.zov_android.data.signalr.SignalR
import com.example.zov_android.data.webrtc.WebRtcManager
import com.example.zov_android.ui.fragments.main.BaseMainFragment
import com.example.zov_android.ui.fragments.navigation.NavigableFragment
import com.example.zov_android.ui.fragments.navigation.NavigationFragment
import com.example.zov_android.ui.fragments.navigation.NavigationInsideFragment
import com.example.zov_android.ui.viewmodels.BaseViewModel
import com.example.zov_android.ui.viewmodels.GuildViewModel
import com.example.zov_android.ui.viewmodels.UserViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.system.exitProcess

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navigationFragment: NavigationFragment

    val gson = Gson()

    private var token: String? = null
    private var authResponse: AuthResponse? = null

    @Inject lateinit var signalR: SignalR
    @Inject lateinit var webRtcManager: WebRtcManager

    private val userViewModel: UserViewModel by viewModels()
    private val guildViewModel: GuildViewModel by viewModels()

    @Inject lateinit var mainRepository: MainRepository
    @Inject lateinit var mainServiceRepository: MainServiceRepository

    private var backPressedTime: Long = 0

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        authResponse = intent.getSerializableExtra("userparams", AuthResponse::class.java)

        checkPermissionsAndStartService()

        val fragmentManager = supportFragmentManager
        val container = findViewById<FrameLayout>(R.id.fragment_container)

        navigationFragment = NavigationFragment(fragmentManager, container)


        if (authResponse == null) {
            Log.e("Token", "AuthResponse is null")
            finish()
            return
        }
        else{
            token = authResponse!!.token
            Log.d("Token", "Полученный token: $token")
        }
        val prefs = getSharedPreferences("app_prefs",Context.MODE_PRIVATE)
        prefs.edit().putString("token", token).apply()

        userViewModel.loadUserData(token)


        lifecycleScope.launch {
            userViewModel.userState.collect{ state->
                when(state){
                    is BaseViewModel.ViewState.Success -> {

                        if(state.data.selectedStatus == null){
                            userViewModel.loadUserSelectedStatus(token!!, StatusRequest(status = "Online"))
                        }

                        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                        prefs.edit().putString("user", gson.toJson(state.data)).apply()
                    }

                    is BaseViewModel.ViewState.Error,
                    BaseViewModel.ViewState.Idle,
                    BaseViewModel.ViewState.Loading->{}
                }

            }
        }

        if (savedInstanceState == null) {
            navigateTo(BaseMainFragment())
        }

        //Log.d("BaseVM","loadUserData")

        //guildViewModel.loadGuildData(GuildRequest("mause"))
        //guildViewModel.loadChannelData(1, ChannelRequest(0,1,"mouse",1,1,2))
    }

    private fun checkPermissionsAndStartService() {
        getCameraAndMicPermission {
            startMyService()
        }
    }

    private fun startMyService() {
        token?.let { mainServiceRepository.startService(it) }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            startMyService()
        } else {
            Toast.makeText(this, "Отказано в разрешениях", Toast.LENGTH_SHORT).show()
        }
    }
    private fun navigateTo(fragment: NavigableFragment) {
        navigationFragment.push(fragment)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val mainFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as? BaseMainFragment
        Log.d("BackPress", "$mainFragment")
        if (mainFragment?.handleBackPress() == true) {
            // Обработка возврата в BaseMainFragment успешна, ничего не делаем
            return
        }

        // Если стек дочерних фрагментов пуст, обрабатываем как обычно
        val currentTime = System.currentTimeMillis()
        if (backPressedTime + 2000 > currentTime) {
            super.onBackPressed()
            finish()
        } else {
            Toast.makeText(this, "Нажмите еще раз для выхода", Toast.LENGTH_SHORT).show()
        }
        backPressedTime = currentTime
    }
}

/*
        Декодировка токена

        val decodedToken = mainRepository.decodingToken(token!!)
        val (timestamp, workerId, sequence) = mainRepository.parseSnowflakeMr(decodedToken.userId)

        /*Log.d("Token", "User ID (Snowflake) token: ${decodedToken.userId}")
        Log.d("Token", "Creation Time token: ${decodedToken.creationTime}")
        Log.d("Token", "Signature token: ${decodedToken.signature}")

        Log.d("Token","Timestamp: $timestamp ms (${Instant.ofEpochMilli(timestamp)})")
        Log.d("Token","Worker ID: $workerId")
        Log.d("Token","Sequence: $sequence")*/*/
package com.example.zov_android.ui.fragments.main

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.lifecycleScope
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.example.zov_android.R
import com.example.zov_android.data.models.response.UserResponse
import com.example.zov_android.data.repository.MainServiceRepository
import com.example.zov_android.data.signalr.SignalR
import com.example.zov_android.data.webrtc.WebRtcManager
import com.example.zov_android.databinding.FragmentCallBinding
import com.example.zov_android.di.qualifiers.User
import com.example.zov_android.domain.service.MainService
import com.example.zov_android.domain.utils.convertToHumanTime
import com.example.zov_android.ui.fragments.navigation.NavigableFragment
import com.example.zov_android.ui.fragments.navigation.NavigationInsideFragment
import com.example.zov_android.ui.viewmodels.BaseViewModel
import com.example.zov_android.ui.viewmodels.GuildViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class CallFragment : NavigableFragment(), MainService.EndCallListener {

    @Inject
    @User
    lateinit var user: UserResponse

    private var _binding: FragmentCallBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var mainServiceRepository: MainServiceRepository

    @Inject lateinit var signalR: SignalR
    @Inject lateinit var webRtcManager: WebRtcManager
    private val guildViewModel: GuildViewModel by viewModels()

    private var microphoneMuted = false
    private var cameraMuted = false

    private var target:String? = null
    private var isVideoCall: Boolean = true
    private var isCaller: Boolean = true

    override fun onCreateView(context: Context): View {
        _binding = FragmentCallBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    @SuppressLint("SetTextI18n")
    private fun init(){
        arguments?.let {
            target = it.getString("target")
            isVideoCall = it.getBoolean("isVideoCall", true)
            isCaller = it.getBoolean("isCaller", true)
        } ?: run {
            requireActivity().finish()
            return
        }

        mainServiceRepository.startService(user.nickname)

        binding.apply {
            callTitleTv.text = "Razdor с $target"

            // Таймер звонка
            var callDuration = 0
            val timerRunnable = object : Runnable {
                override fun run() {
                    callTimerTv.text = callDuration.convertToHumanTime()
                    callDuration++
                    binding.root.postDelayed(this, 1000)
                }
            }
            binding.root.postDelayed(timerRunnable, 1000)

            // Видимость элементов для аудиозвонка
            if (!isVideoCall) {
                toggleCameraButton.isVisible = false
                screenShareButton.isVisible = false
                switchCameraButton.isVisible = false
            }

            // Инициализация SurfaceView
            MainService.remoteSurfaceView = remoteView
            MainService.localSurfaceView = localView

            // Настройка WebRTC
            mainServiceRepository.setupViews(target!!, isVideoCall, isCaller)

            // Обработчики кнопок
            endCallButton.setOnClickListener { mainServiceRepository.sendEndCall() }
            switchCameraButton.setOnClickListener { mainServiceRepository.switchCamera() }
            toggleMicrophoneButton.setOnClickListener { toggleMicrophone() }
            toggleCameraButton.setOnClickListener { toggleCamera() }
        }

        setupSignalRConnection()
        MainService.endCallListener = this
    }

    private fun setupSignalRConnection() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                signalR.startConnection()
                guildViewModel.loadSessionData(1, 1)

                guildViewModel.sessionState.collect { state ->
                    when (state) {
                        is BaseViewModel.ViewState.Success -> {
                            val prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                            prefs.edit().putString("session_id", state.data.sessionId).apply()

                            Log.d("WebRTCData", "Установка связи")
                            webRtcManager.startCall(target!!)
                            Log.d("WebRTCData", "Связь установлена")

                            showToast("Успешное подключение к звонку")
                        }
                        is BaseViewModel.ViewState.Error -> {
                            showToast("Ошибка: ${state.message}")
                        }
                        is BaseViewModel.ViewState.Loading,
                        is BaseViewModel.ViewState.Idle -> {

                        }
                    }
                }
            } catch (e: Exception) {
                showToast("Ошибка подключения: ${e.message}")
                Log.e("CallFragment", "Connection error", e)
            }
        }
    }


    private fun toggleMicrophone() {
        microphoneMuted = !microphoneMuted
        mainServiceRepository.toggleAudio(microphoneMuted)
        binding.toggleMicrophoneButton.setImageResource(
            if (microphoneMuted) R.drawable.ic_mic_off else R.drawable.ic_mic_on
        )
    }

    private fun toggleCamera() {
        cameraMuted = !cameraMuted
        mainServiceRepository.toggleVideo(cameraMuted)
        binding.toggleCameraButton.setImageResource(
            if (cameraMuted) R.drawable.ic_camera_off else R.drawable.ic_camera_on
        )
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun onCallEnded() {
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Освобождаем ресурсы
        webRtcManager.closeConnection()
        MainService.remoteSurfaceView?.release()
        MainService.localSurfaceView?.release()

        MainService.localSurfaceView = null
        MainService.remoteSurfaceView = null
        _binding = null

        lifecycleScope.cancel()
    }
}
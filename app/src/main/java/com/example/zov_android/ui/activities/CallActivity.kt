package com.example.zov_android.ui.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.zov_android.R
import com.example.zov_android.databinding.ActivityCallBinding
import com.example.zov_android.domain.service.MainServiceRepository
import com.example.zov_android.domain.service.MainService
import com.example.zov_android.domain.utils.convertToHumanTime
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class CallActivity: AppCompatActivity(), MainService.EndCallListener {
    private lateinit var views: ActivityCallBinding
    @Inject lateinit var mainServiceRepository: MainServiceRepository

    private var MicrophoneMuted = false
    private var CameraMuted = false

    private var target:String? = null
    private var isVideoCall: Boolean = true
    private var isCaller: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        views = ActivityCallBinding.inflate(layoutInflater)
        setContentView(views.root)
        init()
    }

    @SuppressLint("SetTextI18n")
    private fun init(){
        intent.getStringExtra("target")?.let{
            this.target = it
        }?: kotlin.run {
            finish()
        }

        isVideoCall = intent.getBooleanExtra("isVideoCall", true)
        isCaller = intent.getBooleanExtra("isCaller", true)

        //настраиваем активити вызова
        views.apply {

            callTitleTv.text = "ZoV с ${target}"
            CoroutineScope(Dispatchers.IO).launch{
                for (i in 0..3600){
                    delay(1000)
                    withContext(Dispatchers.Main){
                        // преобразуем int в время
                        callTimerTv.text = i.convertToHumanTime()
                    }
                }
            }


            if(!isVideoCall){
                toggleCameraButton.isVisible = false
                screenShareButton.isVisible = false
                switchCameraButton.isVisible = false
            }

            //устанавливаем вызов
            MainService.remoteSurfaceView = remoteView
            MainService.localSurfaceView = localView
           // Log.d("CallActivity", "localSurfaceView: ${MainService.localSurfaceView}")
            //звонок, скажи клиенту webRTC настроить рендеринг нашего view
            mainServiceRepository.setupViews(target!!, isVideoCall,isCaller)

            endCallButton.setOnClickListener{
                mainServiceRepository.sendEndCall()
            }

            switchCameraButton.setOnClickListener {
                mainServiceRepository.switchCamera()
            }
        }

        setupMicToggleClicked()
        setupCameraToggleClicked()

        MainService.endCallListener = this
    }

    //функционал кнопки микрофона
    private fun setupMicToggleClicked(){
        views.apply {
            toggleMicrophoneButton.setOnClickListener {
                if (!MicrophoneMuted){
                    // -- если микро выключен
                    // отправляем команду
                    mainServiceRepository.toggleAudio(true)
                    // меняем иконку
                    toggleMicrophoneButton.setImageResource(R.drawable.ic_mic_off)
                }else{
                    // -- если включён
                    // возращаем норм статус
                    mainServiceRepository.toggleAudio(false)
                    toggleMicrophoneButton.setImageResource(R.drawable.ic_mic_on)
                }

                MicrophoneMuted = !MicrophoneMuted
            }
        }
    }

    //функционал кнопки камеры
    private fun setupCameraToggleClicked(){
        views.apply {
            toggleCameraButton.setOnClickListener {
                if (!CameraMuted){
                    mainServiceRepository.toggleVideo(true)
                    toggleCameraButton.setImageResource(R.drawable.ic_camera_off)
                }else{
                    mainServiceRepository.toggleVideo(false)
                    toggleCameraButton.setImageResource(R.drawable.ic_camera_on)
                }

                CameraMuted = !CameraMuted
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        mainServiceRepository.sendEndCall()
    }

    //закртываем страницу
    override fun onCallEnded() {
       finish()
    }

    // закрытие служб
    override fun onDestroy() {
        super.onDestroy()
        MainService.remoteSurfaceView?.release()
        MainService.remoteSurfaceView = null

        MainService.localSurfaceView?.release()
        MainService.localSurfaceView = null
    }

}
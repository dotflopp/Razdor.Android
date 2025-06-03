package com.example.zov_android.domain.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.zov_android.R
import com.example.zov_android.data.repository.MainRepository
import com.example.zov_android.domain.service.MainServiceActions
import com.example.zov_android.domain.utils.DataModel
import com.example.zov_android.domain.utils.DataModelType
import com.example.zov_android.domain.utils.isValid
import dagger.hilt.android.AndroidEntryPoint
import org.webrtc.SurfaceViewRenderer
import javax.inject.Inject

@AndroidEntryPoint
class MainService: Service(), MainRepository.Listener { //реализация интерфеса Service

    private var isServiceRunning = false //проверка на запуск
    private var username:String? = null

    @Inject
    lateinit var mainRepository: MainRepository

    private lateinit var notificationManager: NotificationManager

    companion object{
        var listener: Listener? = null
        var endCallListener: EndCallListener? = null

        var screenPermissionIntent : Intent? = null

        var localSurfaceView: SurfaceViewRenderer? = null
            set(value) {
                field = value
                isLocalViewInitialized = value != null
            }
        var remoteSurfaceView: SurfaceViewRenderer? = null
            set(value) {
                field = value
                isRemoteViewInitialized = value != null
            }
        var isLocalViewInitialized = false
        var isRemoteViewInitialized = false

    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NotificationManager::class.java)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let{incomingIntent ->
            when(incomingIntent.action){ //если служба совпадает
                MainServiceActions.START_SERVICE.name -> handleStartService(incomingIntent)
                MainServiceActions.SETUP_VIEWS.name -> handleSetupViews(incomingIntent)
                MainServiceActions.END_CALL.name -> handleEndCall()
                MainServiceActions.SWITCH_CAMERA.name -> handleSwitchCamera()
                MainServiceActions.TOGGLE_AUDIO.name -> handleToggleAudio(incomingIntent)
                MainServiceActions.TOGGLE_VIDEO.name -> handleToggleVideo(incomingIntent)
                MainServiceActions.STOP_SERVICE.name -> handleStopService()
                else -> Unit
            }
        }
        return START_STICKY
    }

    private fun handleSetupViews(incomingIntent: Intent) {
        val target = incomingIntent.getStringExtra("target")
        val isVideoCall = incomingIntent.getBooleanExtra("isVideoCall", true)
        val isCaller = incomingIntent.getBooleanExtra("isCaller", false)

        // Проверка инициализации через флаги
        /*if (isLocalViewInitialized || isRemoteViewInitialized) {
            Log.e("MainService", "Views already initialized. Local: $isLocalViewInitialized, Remote: $isRemoteViewInitialized")
            return
        }*/

        try {
            target?.let { mainRepository.setTarget(it) }

            // Инициализация с проверкой
            mainRepository.initLocalSurfaceView(localSurfaceView!!, isVideoCall)
            mainRepository.initRemoteSurfaceView(remoteSurfaceView!!)
            //mainRepository.startCall()

        } catch (e: Exception) {
            Log.e("MainService", "Error initializing views: ${e.message}")
            stopSelf()
        }
    }

    //запуск службы
    private fun handleStartService(incomingIntent: Intent){

        if(!isServiceRunning){
            isServiceRunning = true
            username = incomingIntent.getStringExtra("username")

            startServiceWithNotification() //запуск службы с уведомлением, обязательно

            //настройка клиентов

            mainRepository.listener = this // получаем последний полученный ласт ивент
            // инициализируем наш webRTC клиент
            mainRepository.initWebRtcClient(username!!)
        }
    }

    private fun startServiceWithNotification() {

        //создаём канал уведомлений
        val channel  = NotificationChannel(
            "channel1", "foreground", NotificationManager.IMPORTANCE_HIGH
        )

        val intent = Intent(this, MainServiceReceiver::class.java).apply {
            action = "ACTION_EXIT"
        }
        val pendingIntent : PendingIntent =
            PendingIntent.getBroadcast(this,0 ,intent,PendingIntent.FLAG_IMMUTABLE)

        notificationManager.createNotificationChannel(channel)
        // создаём уведомление
        val notification = NotificationCompat.Builder(
            this, "channel1",
        )   .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Service Running")
            .setContentText("Foreground service is active")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(R.drawable.ic_end_call,"Закончить",pendingIntent)

        //ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA
        startForeground(1, notification.build())

    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    override fun onLatestEventReceived(dataModel: DataModel) {
        if(dataModel.isValid()){
            when(dataModel.type){
                DataModelType.StartVideoCall,
                DataModelType.StartAudioCall -> {
                    listener?.onCallReceived(dataModel)
                }

                else -> Unit
            }
        }
    }

    private fun handleToggleVideo(incomingIntent: Intent) {
        val shouldBeMuted = incomingIntent.getBooleanExtra("shouldBeMuted",true)
       // this.isPreviousCallStateVideo = !shouldBeMuted
        mainRepository.toggleVideo(shouldBeMuted)
    }

    private fun handleToggleAudio(incomingIntent: Intent) {
        val shouldBeMuted = incomingIntent.getBooleanExtra("shouldBeMuted",true)
        mainRepository.toggleAudio(shouldBeMuted)
    }

    private fun handleSwitchCamera() {
        mainRepository.switchCamera()
    }

    private fun handleEndCall() {
        // сигнал другому участнику, что созвон завершён
        mainRepository.sendEndCall()
        // завершаем звонок и перезапускаем клент webRTC
        endCallAndRestartRepository()
    }

    private fun handleStopService(){
       /* mainRepository.endCall()
        mainRepository.logOff(){
            isServiceRunning = false
            stopSelf()
        }*/
    }

    private fun endCallAndRestartRepository(){
       /* mainRepository.endCall()
        endCallListener?.onCallEnded()
        mainRepository.initWebRtcClient(username!!)*/
    }

    override fun endCall() {
        // получени сигнал о завершении вызова от собеседника
        endCallAndRestartRepository()
    }

    interface EndCallListener {
        fun onCallEnded()
    }

    interface Listener{
        //будет вызываться при получении вызова
        fun onCallReceived(model: DataModel)
    }
}
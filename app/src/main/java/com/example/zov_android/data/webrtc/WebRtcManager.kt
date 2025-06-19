package com.example.zov_android.data.webrtc

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.example.zov_android.data.signalr.SignalR
import com.example.zov_android.data.websocket.WebSocketClient
import com.example.zov_android.di.qualifiers.SessionId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import org.webrtc.AudioTrack
import org.webrtc.Camera2Enumerator
import org.webrtc.CameraVideoCapturer
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.EglBase
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.MediaStreamTrack
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.SessionDescription
import org.webrtc.SurfaceTextureHelper
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoTrack
import java.net.URI
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebRtcManager @Inject constructor(
    private val signalR: SignalR,
    @SessionId private val sessionId: String, // sessionId берется из SessionModule
    private val context: Context,
) {
    private lateinit var peerConnection: PeerConnection

    private lateinit var webSocketClient: WebSocketClient

    private var isWebSocketReady = false
    private var isWebSocketConnected = false

    private lateinit var username: String

    private val peerConnectionFactory by lazy {createPeerConnectionFactory()}

    private lateinit var localSurfaceView: SurfaceViewRenderer
    private lateinit var remoteSurfaceView: SurfaceViewRenderer

    private var localAudioTrack: AudioTrack? = null
    private var localVideoTrack: VideoTrack? = null
    private var remoteVideoTrack: VideoTrack? = null

    private var localStream: MediaStream? = null
    private var localTrackId = "" //локальные идентификаторы
    private var localStreamId = ""


    private val videoCapturer = getVideoCapturer(context)


    private val localVideoSource by lazy { peerConnectionFactory.createVideoSource(false) }
    private val localAudioSource by lazy { peerConnectionFactory.createAudioSource(MediaConstraints())}

    private var surfaceTextureHelper: SurfaceTextureHelper? = null

    private val eglBaseContext = EglBase.create().eglBaseContext

    private val pendingCandidates = mutableListOf<IceCandidate>()

    // разрешения для связи
    private val mediaConstraint = MediaConstraints().apply {
        mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo","true"))
        mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio","true"))
    }


    private var isInitiator = false
    private var roomState: RoomState = RoomState.IDLE
    private val pendingLocalIceCandidates = mutableListOf<IceCandidate>()

    sealed class RoomState {
        object IDLE : RoomState()
        object JOINING : RoomState()
        object READY : RoomState()
        object ERROR : RoomState()
    }

    init{
        Log.d("WebRTCData", "Инициализация PeerConnectionFactory...")
        initPeerConnectionFactory()
        Log.d("WebRTCData", "PeerConnectionFactory инициализирована")
        connectToWebSocket()
    }

    private fun connectToWebSocket() {
        if (isWebSocketConnected) return
        val uri = URI("ws://10.0.2.2:8080")
        webSocketClient = WebSocketClient(uri) { message ->
            handleWebSocketMessage(message)
        }
        webSocketClient.connect()
        isWebSocketConnected = true
    }

    fun initializeWebrtcClient(
        username: String,
        //observer: MyPeerObserver
    ) {
        Log.d("WebRTCData", "Инициализация WebRTC-клиента для пользователя: $username")
        this.username = username

        localTrackId = "${username}_track"
        localStreamId = "${username}_stream"

        //setupSignalRHandlers()
        Log.d("WebRTCData", "Обработчики SignalR зарегистрированы")

        peerConnection = createPeerConnection()?.also {
            Log.d("WebRTCData", "PeerConnection создана: ${it.toString()}")
        } ?: run {
            Log.e("WebRTCData", "Ошибка создания PeerConnection")
            throw IllegalStateException("Не удалось создать PeerConnection")
        }

    }

    private fun handleWebSocketMessage(message: String) {
        try {
            val json = JSONObject(message)
            val type = json.optString("type") ?: return

            Log.d("WebSocket", "Получено сообщение: $message")

            when (type) {
                "error" -> {
                    val errorMessage = json.optString("message", "Неизвестная ошибка")
                    Log.e("WebSocket", "Ошибка от сервера: $errorMessage")
                    if (errorMessage == "Room is full") {
                        Handler(Looper.getMainLooper()).post {
                            Toast.makeText(context, "Комната заполнена", Toast.LENGTH_LONG).show()
                        }
                        roomState = RoomState.ERROR
                    }
                }
                "room-ready" -> {
                    val clientIndex = json.optInt("clientIndex", -1)
                    isInitiator = (clientIndex == 0)
                    roomState = RoomState.READY
                    Log.d("WebRTCData", "Комната готова. Инициатор: $isInitiator")

                    if (isInitiator) {
                        createOffer()
                    }
                    sendPendingLocalCandidates()
                }
                "peer-disconnected" -> {
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(context, "Участник отключился", Toast.LENGTH_LONG).show()
                    }
                    closeConnection()
                }
                "offer" -> {
                    val payload = json.getString("payload")
                    Log.d("WebSocket", "Получен Offer: $payload")
                    onRemoteSessionReceived(SessionDescription(SessionDescription.Type.OFFER, payload))
                    createAnswer()
                }
                "answer" -> {
                    val payload = json.getString("payload")
                    Log.d("WebSocket", "Получен Answer: $payload")
                    onRemoteSessionReceived(SessionDescription(SessionDescription.Type.ANSWER, payload))
                    pendingCandidates.forEach { addIceCandidateToPeer(it) }
                    pendingCandidates.clear()
                }
                "ice-candidate" -> {
                    val candidateObj = json.getJSONObject("payload")
                    val sdpMid = candidateObj.optString("sdpMid") ?: run {
                        Log.e("WebSocket", "Отсутствует поле sdpMid в ICE-кандидате")
                        return
                    }
                    val sdpMLineIndex = candidateObj.optInt("sdpMLineIndex", -1)
                    val candidateSdp = candidateObj.optString("candidate") ?: run {
                        Log.e("WebSocket", "Отсутствует поле candidate в ICE-кандидате")
                        return
                    }

                    val iceCandidate = IceCandidate(sdpMid, sdpMLineIndex, candidateSdp)
                    if (peerConnection.remoteDescription == null) {
                        pendingCandidates.add(iceCandidate)
                    } else {
                        addIceCandidateToPeer(iceCandidate)
                    }
                }
                else -> Log.w("WebSocket", "Неизвестный тип сообщения: $type")
            }
        } catch (e: JSONException) {
            Log.e("WebSocket", "Ошибка парсинга JSON: $message", e)
        }

        // Обработчик Offer
        /**signalR.connection.on("Offer", { desc ->
            Log.d("WebRTC-SignalR", "Получен Offer: ${desc.description}")
            coroutineScope.launch {
                try {
                    onRemoteSessionReceived(desc)
                    createAnswer()
                } catch (e: Exception) {
                    Log.e("WebRTCData", "Ошибка обработки Offer", e)
                }
            }
        }, SessionDescription::class.java)*/

        // Обработчик Answer
        /*signalR.connection.on("Answer", { desc ->
            Log.d("WebRTC-SignalR", "Получен Answer: ${desc.description}")
            coroutineScope.launch {
                try {
                    onRemoteSessionReceived(desc)

                    for(candidate in pendingCandidates){
                        addIceCandidateToPeer(candidate)
                    }
                    pendingCandidates.clear()

                    signalR.connection.send("end-call", sessionId)
                    Log.d("WebRTCData", "Удаленное описание обработано")
                } catch (e: Exception) {
                    Log.e("WebRTCData", "Ошибка обработки Answer", e)
                }
            }
        }, SessionDescription::class.java)*/

        // Обработчик IceCandidate
        /*signalR.connection.on("Icecandidate", { candidate: IceCandidate ->
            Log.d("WebRTC-SignalR", "Получен ICE-кандидат: ${candidate.sdpMid}")
            try {
                if(peerConnection.remoteDescription == null){
                    pendingCandidates.add(candidate)
                }
                else{
                    addIceCandidateToPeer(candidate)
                }
                Log.d("WebRTCData", "ICE-кандидат обработан")
            } catch (e: Exception) {
                Log.e("WebRTCData", "Ошибка обработки ICE-кандидата", e)
            }
        }, IceCandidate::class.java)*/
    }

    //// создание фабрики одноранговых сетей
    private fun initPeerConnectionFactory() {
        try {
            val option = PeerConnectionFactory.InitializationOptions.builder(context)
                .setEnableInternalTracer(true)
                .setFieldTrials("WebRTC-H264HighProfile/Enabled/") //функция улучшения качества видео
                .createInitializationOptions()

            PeerConnectionFactory.initialize(option)

            Log.i("WebRTCData", "PeerConnectionFactory успешно инициализирована")
        } catch (e: Exception) {
            Log.e("WebRTCData", "Ошибка инициализации PeerConnectionFactory", e)
        }
    }

    private fun createPeerConnectionFactory(): PeerConnectionFactory {
        return PeerConnectionFactory.builder()
            .setVideoDecoderFactory(
                DefaultVideoDecoderFactory(eglBaseContext)
            ).setVideoEncoderFactory(
                DefaultVideoEncoderFactory(
                    eglBaseContext,
                    true,  // enableIntelVp8Encoder
                    true   // enableH264HighProfile
                )
            )
            .createPeerConnectionFactory()
    }

    //// создание медиапотока

    private fun startLocalStreaming(localView: SurfaceViewRenderer, isVideoCall: Boolean) {
        Log.d("WebRTC-Media", "Запуск локального стриминга")
        try {
            localStream = peerConnectionFactory.createLocalMediaStream(localStreamId)
            Log.d("WebRTC-Media", "Локальный медиапоток создан")


            localAudioTrack = peerConnectionFactory.createAudioTrack("${localTrackId}_audio", localAudioSource)
            localStream?.addTrack(localAudioTrack)
            Log.d("WebRTC-Media", "Аудиодорожка создана и добавлена")

            if (isVideoCall) {
                startCapturingCamera(localView)
            }

            peerConnection.addStream(localStream)
            Log.i("WebRTC-Media", "Локальный поток добавлен в PeerConnection")
        } catch (e: Exception) {
            Log.e("WebRTC-Media", "Ошибка запуска локального стриминга", e)
        }
    }


    private fun startCapturingCamera(localView: SurfaceViewRenderer) {
        Log.d("WebRTC-Media", "Запуск захвата камеры...")
        try {

            surfaceTextureHelper = SurfaceTextureHelper.create(
                Thread.currentThread().name, eglBaseContext
            )

            videoCapturer.initialize(
                surfaceTextureHelper,
                context,
                localVideoSource.capturerObserver
            )

            videoCapturer.startCapture(720, 480, 20)
            Log.i("WebRTC-Media", "Захват камеры запущен")

            localVideoTrack = peerConnectionFactory.createVideoTrack("${localTrackId}_video", localVideoSource)
            localVideoTrack?.addSink(localView) // отображение видео на экране
            localStream?.addTrack(localVideoTrack)


        } catch (e: Exception) {
            Log.e("WebRTC-Media", "Ошибка захвата камеры", e)
        }
    }

    //видеозахват
    private fun getVideoCapturer(context: Context): CameraVideoCapturer =
        Camera2Enumerator(context).run {
            deviceNames.find { isFrontFacing(it) }?.let {
                createCapturer(it,null)
            }?:deviceNames.find {isBackFacing(it) }?.let {
                createCapturer(it,null)
            }?:throw IllegalStateException("Камера не найдена")
        }


    //// создание PeerConnection

    private val iceServers = listOf(
        //свой сервер для созвонов

        PeerConnection.IceServer.builder("turn:fr-turn2.xirsys.com:3478?transport=udp")
            .setUsername("b_x8c3H9otu8vC-LwmnAsPdEQnWlh_zHf54JGX8KJx2wBztiX1udhli1_MK6sxHMAAAAAGcuKjdMdWt1cw==")
            .setPassword("c8628fa0-9de3-11ef-a83d-0242ac120004").createIceServer(),

        //PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer()

    )

    private val rtcConfig = PeerConnection.RTCConfiguration(iceServers)

    private fun createPeerConnection(): PeerConnection? {
        Log.d("WebRTCData", "Создание PeerConnection...")
        return peerConnectionFactory.createPeerConnection(rtcConfig, object: MyPeerObserver(){
            override fun onIceCandidate(candidate: IceCandidate) {
                pendingLocalIceCandidates.add(candidate)
                sendPendingLocalCandidates()
            }

            override fun onAddStream(stream: MediaStream) {
                Log.d("WebRTC-Stream", "Получен удалённый поток: ${stream.id}")
                if (stream.videoTracks.isNotEmpty()) {
                    remoteVideoTrack = stream.videoTracks.first()
                    Log.d("WebRTC-Stream", "Видео-трек найден: ${remoteVideoTrack?.id()}")

                    // Убедимся, что поверхность инициализирована
                    if (::remoteSurfaceView.isInitialized) {
                        remoteVideoTrack?.addSink(remoteSurfaceView)

                        Log.d("WebRTC-Stream", "Видео добавлено на SurfaceView")
                    } else {
                        Log.e("WebRTC-Stream", "Remote SurfaceView не инициализирован!")
                    }
                } else {
                    Log.w("WebRTC-Stream", "Видео-треки отсутствуют в потоке")
                }
            }

        })?.also {
            Log.d("WebRTCData", "PeerConnection успешно создана")
        }
    }

    private fun sendPendingLocalCandidates() {
        if (roomState != RoomState.READY) {
            Log.d("WebRTC", "Комната не готова, кандидат сохранен в буфер")
            return
        }

        pendingLocalIceCandidates.forEach { candidate ->
            webSocketClient.sendIceCandidate(sessionId, candidate)
        }
        pendingLocalIceCandidates.clear()
    }


    //// Установка соединения

    private fun createOffer() {
        if (!isWebSocketReady) {
            Log.w("WebRTCData", "WebSocket не подключен. Offer не создан")
            return
        }
        Log.d("WebRTCData", "Создание Offer...")
        peerConnection.createOffer(object : MySdpObserver() {
            override fun onCreateSuccess(desc: SessionDescription?) {
                super.onCreateSuccess(desc)
                Log.d("WebRTCData", "Offer успешно создан: ${desc?.type}")
                desc ?: return

                peerConnection.setLocalDescription(object : MySdpObserver() {}, desc)
                Log.d("WebRTCData", "Локальное описание установлено для Offer")

                webSocketClient.sendOffer(sessionId, desc.description)
                Log.i("WebRTCData", "Offer отправлен через WebSocket")
            }
        }, mediaConstraint)
    }

    private fun createAnswer() {
        Log.d("WebRTCData", "Создание Answer...")
        peerConnection.createAnswer(object : MySdpObserver() {
            override fun onCreateSuccess(desc: SessionDescription?) {
                super.onCreateSuccess(desc)
                Log.d("WebRTCData", "Answer успешно создан: ${desc?.type}")
                desc ?: return

                peerConnection.setLocalDescription(object : MySdpObserver() {}, desc)

                webSocketClient.sendAnswer(sessionId, desc.description)
                Log.i("WebRTCData", "Answer отправлен через WebSocket")
            }

        }, mediaConstraint)
    }

    //// Обмен ICE-кандидатами

    // Прием кандидата
    private fun addIceCandidateToPeer(iceCandidate: IceCandidate) {
        Log.d("WebRTCData", "Добавление ICE-кандидата: ${iceCandidate.sdpMid}")
        peerConnection.addIceCandidate(iceCandidate)
    }

    // Отправка в создании PeerConnection


    // установка данных, когда начинается удалённый сеанс
    private fun onRemoteSessionReceived(sessionDescription: SessionDescription) {
        Log.d("WebRTCData", "Установка удаленного описания...")
        peerConnection.setRemoteDescription(MySdpObserver(), sessionDescription)
        // Добавляем накопленные кандидаты
        for (candidate in pendingCandidates) {
            addIceCandidateToPeer(candidate)
        }
        pendingCandidates.clear()
    }

    ////  Отображение видео
    private fun initSurfaceView(view: SurfaceViewRenderer) {
        Log.d("WebRTC-Media", "Инициализация SurfaceView...")
        view.run {
            setMirror(false)
            setEnableHardwareScaler(true)
            init(eglBaseContext, null)
        }
    }
    fun initLocalSurfaceView(localView: SurfaceViewRenderer, isVideoCall: Boolean) {
        Log.d("WebRTC-Media", "Инициализация локального SurfaceView")
        this.localSurfaceView = localView
        initSurfaceView(localView)
        startLocalStreaming(localView, isVideoCall)
    }

    fun initRemoteSurfaceView(remoteView: SurfaceViewRenderer) {
        Log.d("WebRTC-Media", "Инициализация удаленного SurfaceView")
        this.remoteSurfaceView = remoteView
        initSurfaceView(remoteView)
    }

    //// остальная часть кода

    fun closeConnection() {
        Log.d("WebRTCData", "Закрытие соединения...")
        try {
            // 1. Остановить захват видео
            try {
                videoCapturer.stopCapture()
            } catch (e: Exception) {
                Log.e("WebRTC-Media", "Ошибка остановки видеозахвата", e)
            }

            // 2. Освободить видеозахватчик
            try {
                videoCapturer.dispose()
                Log.d("WebRTC-Media", "Видеозахватчик освобожден")
            } catch (e: Exception) {
                Log.e("WebRTC-Media", "Ошибка освобождения видеозахватчика", e)
            }

            // 3. Освободить SurfaceTextureHelper
            surfaceTextureHelper?.dispose()
            surfaceTextureHelper = null

            // 4. Удалить треки из потока перед освобождением
            localStream?.videoTracks?.forEach { track ->
                track.removeSink(localSurfaceView)
                localStream?.removeTrack(track)
            }
            localStream?.audioTracks?.forEach { track ->
                localStream?.removeTrack(track)
            }

            // 5. Закрыть PeerConnection
            peerConnection.close()
            Log.i("WebRTCData", "Соединение закрыто")

            // 6. Освободить ресурсы в правильном порядке
            CoroutineScope(Dispatchers.Main).launch {
                localSurfaceView.clearImage()
                remoteSurfaceView.clearImage()
                localSurfaceView.release()
                remoteSurfaceView.release()
            }

            // 7. Освободить треки (только если они еще не освобождены)
            localVideoTrack?.let {
                if (!it.isDisposed) {
                    it.dispose()
                }
                localVideoTrack = null
            }

            remoteVideoTrack?.let {
                if (!it.isDisposed) {
                    it.dispose()
                }
                remoteVideoTrack = null
            }

            // 8. Освободить медиапоток
            localStream?.dispose()
            localStream = null
            Log.d("WebRTC-Media", "Локальный медиапоток освобожден")

            webSocketClient.close()
            roomState = RoomState.IDLE
            pendingLocalIceCandidates.clear()
            webSocketClient.close()
            webSocketClient.connect()

        } catch (e: Exception) {
            Log.e("WebRTCData", "Ошибка при закрытии соединения", e)
        }
    }

    fun switchCamera() {
        Log.d("WebRTC-Media", "Переключение камеры...")
        try {
            videoCapturer.switchCamera(null)
            Log.i("WebRTC-Media", "Камера переключена")
        } catch (e: Exception) {
            Log.e("WebRTC-Media", "Ошибка переключения камеры", e)
        }
    }

    fun toggleAudio(shouldBeMuted: Boolean) {
        localAudioTrack?.setEnabled(!shouldBeMuted)
    }

    fun toggleVideo(shouldBeMuted: Boolean) {
        if (shouldBeMuted) {
            stopCapturingCamera()
        } else {
            startCapturingCamera(localSurfaceView)
        }
    }


    private fun stopCapturingCamera() {
        try {
            videoCapturer.stopCapture()
            localVideoTrack?.removeSink(localSurfaceView)
            localSurfaceView.clearImage()
        } catch (e: Exception) {
            Log.e("WebRTC-Media", "Ошибка остановки камеры", e)
        }
    }


    suspend fun startCall(nickname: String) {
        if (!webSocketClient.isOpen) {
            webSocketClient.reconnectBlocking()
        }
        isWebSocketReady = true

        // Отправляем сообщение о присоединении к комнате
        val joinMessage = JSONObject().apply {
            put("type", "join")
            put("sessionId", sessionId)
        }
        webSocketClient.send(joinMessage.toString())
        roomState = RoomState.JOINING
        Log.d("WebRTCData", "Отправлен join-запрос для комнаты $sessionId")
    }

    private suspend fun reconnectAndRetry(nickname: String, attempts: Int = 3) {
        for (i in 1..attempts) {
            delay(2000) // Задержка перед повторной попыткой
            if (webSocketClient.isOpen) {
                Log.d("WebRTCData", "Повторная попытка $i: WebSocket подключен")
                isWebSocketReady = true
                createOffer()
                return
            }
        }
        Log.e("WebRTCData", "Не удалось установить WebSocket-соединение")
    }

    val MediaStreamTrack.isDisposed: Boolean
        get() = try {
            this.id() // Простая проверка состояния
            false
        } catch (e: IllegalStateException) {
            true
        }
}
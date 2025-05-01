package com.example.zov_android.data.webrtc

import android.content.Context
import android.content.Intent
import com.example.zov_android.domain.utils.DataModel
import com.example.zov_android.domain.utils.DataModelType
import com.google.gson.Gson
import org.webrtc.AudioTrack
import org.webrtc.Camera2Enumerator
import org.webrtc.CameraVideoCapturer
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.EglBase
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.SessionDescription
import org.webrtc.SurfaceTextureHelper
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoCapturer
import org.webrtc.VideoTrack
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ClientWebRTC @Inject constructor(
    private val context: Context,
    private val gson: Gson
){

    var listener: Listener? = null
    private lateinit var username: String

    //webrtc переменные
    private val eglBaseContext = EglBase.create().eglBaseContext

    private val peerConnectionFactory by lazy {createPeerConnectionFactory()}
    private var peerConnection: PeerConnection? = null


    private val iceServer = listOf( //свой сервер для созвонов

        /*PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer(),

        PeerConnection.IceServer.builder("stun:global.stun.twilio.com:3478").createIceServer(),*/

        /*PeerConnection.IceServer.builder("turn:fr-turn2.xirsys.com:80?transport=tcp")
            .setUsername("b_x8c3H9otu8vC-LwmnAsPdEQnWlh_zHf54JGX8KJx2wBztiX1udhli1_MK6sxHMAAAAAGcuKjdMdWt1cw==")
            .setPassword("c8628fa0-9de3-11ef-a83d-0242ac120004").createIceServer(),*/

        /*PeerConnection.IceServer.builder("turn:fr-turn2.xirsys.com:80?transport=udp")
            .setUsername("b_x8c3H9otu8vC-LwmnAsPdEQnWlh_zHf54JGX8KJx2wBztiX1udhli1_MK6sxHMAAAAAGcuKjdMdWt1cw==")
            .setPassword("c8628fa0-9de3-11ef-a83d-0242ac120004").createIceServer(),

        PeerConnection.IceServer.builder("turn:fr-turn2.xirsys.com:3478?transport=tcp")
            .setUsername("b_x8c3H9otu8vC-LwmnAsPdEQnWlh_zHf54JGX8KJx2wBztiX1udhli1_MK6sxHMAAAAAGcuKjdMdWt1cw==")
            .setPassword("c8628fa0-9de3-11ef-a83d-0242ac120004").createIceServer(),*/

        PeerConnection.IceServer.builder("turn:fr-turn2.xirsys.com:3478?transport=udp")
            .setUsername("b_x8c3H9otu8vC-LwmnAsPdEQnWlh_zHf54JGX8KJx2wBztiX1udhli1_MK6sxHMAAAAAGcuKjdMdWt1cw==")
            .setPassword("c8628fa0-9de3-11ef-a83d-0242ac120004").createIceServer(),

        /*PeerConnection.IceServer.builder("turns:fr-turn2.xirsys.com:443?transport=tcp")
            .setUsername("b_x8c3H9otu8vC-LwmnAsPdEQnWlh_zHf54JGX8KJx2wBztiX1udhli1_MK6sxHMAAAAAGcuKjdMdWt1cw==")
            .setPassword("c8628fa0-9de3-11ef-a83d-0242ac120004").createIceServer(),

        PeerConnection.IceServer.builder("turns:fr-turn2.xirsys.com:5349?transport=tcp")
            .setUsername("b_x8c3H9otu8vC-LwmnAsPdEQnWlh_zHf54JGX8KJx2wBztiX1udhli1_MK6sxHMAAAAAGcuKjdMdWt1cw==")
            .setPassword("c8628fa0-9de3-11ef-a83d-0242ac120004").createIceServer()*/

    )

    private val localVideoSource by lazy { peerConnectionFactory.createVideoSource(false) }
    private val localAudioSource by lazy { peerConnectionFactory.createAudioSource(MediaConstraints())}

    private val videoCapturer = getVideoCapturer(context)

    private var surfaceTextureHelper:SurfaceTextureHelper? = null

    // разрешения для связи
    private val mediaConstraint = MediaConstraints().apply {
        mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo","true"))
        mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio","true"))
    }

    //переменные вызова
    private lateinit var localSurfaceView: SurfaceViewRenderer
    private lateinit var remoteSurfaceView: SurfaceViewRenderer

    private var localStream: MediaStream? = null
    private var localTrackId = "" //локальные идентификаторы
    private var localStreamId = ""

    private var localAudioTrack: AudioTrack?=null
    private var localVideoTrack: VideoTrack?=null


    //переменные экрана
    private var permissionIntent: Intent?=null
    private var screenCapturer: VideoCapturer?=null
    private val localScreenVideoSource by lazy { peerConnectionFactory.createVideoSource(false) }
    private var localScreenShareVideoTrack:VideoTrack?=null


    //раздел создания соединения
    init{
        initPeerConnectionFactory()
    }
    //фабрика одноранговых сетей
    private fun initPeerConnectionFactory() {
        val option = PeerConnectionFactory.InitializationOptions.builder(context)
            .setEnableInternalTracer(true).setFieldTrials("WebRTC-H264HighProfile/Enabled/")
            .createInitializationOptions()
        PeerConnectionFactory.initialize(option)
    }
    private fun createPeerConnectionFactory(): PeerConnectionFactory {
        return PeerConnectionFactory.builder()
            .setVideoDecoderFactory(
                DefaultVideoDecoderFactory(eglBaseContext)
            ).setVideoEncoderFactory(
                DefaultVideoEncoderFactory(
                    eglBaseContext, true, true
                )
            ).setOptions(PeerConnectionFactory.Options().apply {
                disableNetworkMonitor = false
                disableEncryption = false
            }).createPeerConnectionFactory()
    }
    fun initializeWebrtcClient(
        username: String, observer: PeerConnection.Observer
    ) {
        this.username = username

        localTrackId = "${username}_track"
        localStreamId = "${username}_stream"

        peerConnection = createPeerConnection(observer)
    }
    private fun createPeerConnection(observer: PeerConnection.Observer): PeerConnection? {
        return peerConnectionFactory.createPeerConnection(iceServer, observer)
    }

    //раздел созвона
    fun call(target:String){
        peerConnection?.createOffer(object : MySdpObserver() {
            override fun onCreateSuccess(desc: SessionDescription?) { //desc - описание сеанса
                super.onCreateSuccess(desc)
                peerConnection?.setLocalDescription(object : MySdpObserver() { // установка описания
                    override fun onSetSuccess() {
                        super.onSetSuccess()
                        listener?.onTransferEventToSocket( // передача описания другому пользователю
                            DataModel(
                                type = DataModelType.Offer,
                                sender = username,
                                target = target,
                                data = desc?.description)
                        )
                    }
                },desc)
            }
        },mediaConstraint)
    }
    fun answer(target:String){ //тоже самое, что и call, кроме типа данных
        peerConnection?.createAnswer(object : MySdpObserver() {
            override fun onCreateSuccess(desc: SessionDescription?) {
                super.onCreateSuccess(desc)
                peerConnection?.setLocalDescription(object : MySdpObserver() {
                    override fun onSetSuccess() {
                        super.onSetSuccess()
                        listener?.onTransferEventToSocket(
                            DataModel(
                                type = DataModelType.Answer,
                                sender = username,
                                target = target,
                                data = desc?.description)
                        )
                    }
                },desc)
            }
        },mediaConstraint)
    }
    // установка данных, когда начинается удалённый сеанс
    fun onRemoteSessionReceived(sessionDescription: SessionDescription){
        peerConnection?.setRemoteDescription(MySdpObserver(),sessionDescription)
    }
    // добавляем кандидатов в соединение
    fun addIceCandidateToPeer(iceCandidate: IceCandidate){
        peerConnection?.addIceCandidate(iceCandidate)
    }
    // отправка кондидата
    fun sendIceCandidate(target: String, iceCandidate: IceCandidate){
        addIceCandidateToPeer(iceCandidate)
        listener?.onTransferEventToSocket(
            DataModel(
                type = DataModelType.IceCandidates,
                sender = username,
                target = target,
                data = gson.toJson(iceCandidate)
            )
        )
    }
    fun closeConnection(){
        try {
            videoCapturer.dispose()
            //screenCapturer?.dispose()
            localStream?.dispose()
            peerConnection?.close()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    fun switchCamera(){
        videoCapturer.switchCamera(null)
    }
    // отключение звука
    fun toggleAudio(shouldBeMuted:Boolean){
        if (shouldBeMuted){
            localStream?.removeTrack(localAudioTrack)
        }else{
            localStream?.addTrack(localAudioTrack)
        }
    }
    // отключение видео
    fun toggleVideo(shouldBeMuted: Boolean){
        try {
            if (shouldBeMuted){
                stopCapturingCamera()
            }else{
                startCapturingCamera(localSurfaceView)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    //раздел потоковых передач
    // рендеринг view
    private fun initSurfaceView(view: SurfaceViewRenderer) {
        view.run {
            setMirror(false)
            setEnableHardwareScaler(true)
            init(eglBaseContext, null)
        }
    }
    fun initLocalSurfaceView(localView: SurfaceViewRenderer, isVideoCall: Boolean) {
        this.localSurfaceView = localView
        initSurfaceView(localView)
        startLocalStreaming(localView, isVideoCall)
    }
    fun initRemoteSurfaceView(view:SurfaceViewRenderer){
        this.remoteSurfaceView = view
        initSurfaceView(view)
    }
    private fun startLocalStreaming(localView: SurfaceViewRenderer, isVideoCall: Boolean) {
        localStream = peerConnectionFactory.createLocalMediaStream(localStreamId)
        if (isVideoCall){
            startCapturingCamera(localView)
        }

        //создаём аудиодорожку
        localAudioTrack = peerConnectionFactory.createAudioTrack(localTrackId+"_audio",localAudioSource)
        localStream?.addTrack(localAudioTrack) // добавляем дорожку в поток
        peerConnection?.addStream(localStream) // добавялем поток в подключение
    }
    private fun startCapturingCamera(localView: SurfaceViewRenderer){
        surfaceTextureHelper = SurfaceTextureHelper.create(
            Thread.currentThread().name, eglBaseContext
        )

        videoCapturer.initialize(
            surfaceTextureHelper, context, localVideoSource.capturerObserver
        )

        videoCapturer.startCapture(
            720,480,20 // разрешение и кол-во кадров в сек
        )

        // повторяем тоже самое, что и с аудио
        localVideoTrack = peerConnectionFactory.createVideoTrack(localTrackId+"_video",localVideoSource)
        localVideoTrack?.addSink(localView)
        localStream?.addTrack(localVideoTrack)

    }
    private fun stopCapturingCamera(){
        videoCapturer.dispose() // удаление видеозахвата
        //удаляем соединение между источником видеопотока (например, видеозахватом) и отображением на экране (SurfaceView)
        localVideoTrack?.removeSink(localSurfaceView)
        //очищаем текущее изображение, отображаемое на SurfaceView
        localSurfaceView.clearImage()
        //удаляем видеотрек из потока
        localStream?.removeTrack(localVideoTrack)
        // освобождаем ресурсы, связанные с видеотреком
        localVideoTrack?.dispose()
    }
    //видеозахват
    private fun getVideoCapturer(context: Context): CameraVideoCapturer =
        Camera2Enumerator(context).run {
            deviceNames.find { //проверка направления камеры
                isFrontFacing(it)
            }?.let {
                createCapturer(it,null)
            }?:throw IllegalStateException()
        }

    interface Listener{
        fun onTransferEventToSocket(data: DataModel)
    }

}
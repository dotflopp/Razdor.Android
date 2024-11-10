package com.example.zov_android.repository

import android.util.Log
import com.example.zov_android.firebaseClient.FirebaseClient
import com.example.zov_android.service.MainService
import com.example.zov_android.utils.DataModel
import com.example.zov_android.utils.DataModelType
import com.example.zov_android.utils.UserStatus
import com.example.zov_android.webrtc.ClientWebRTC
import com.example.zov_android.webrtc.MyPeerObserver
import com.google.gson.Gson
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.SessionDescription
import org.webrtc.SurfaceViewRenderer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepository @Inject constructor(
    private val firebaseClient: FirebaseClient,
    private val clientWebRTC: ClientWebRTC,
    private val gson: Gson
) : ClientWebRTC.Listener {

    private var target: String? = null
    var listener: Listener? = null
    private var remoteView:SurfaceViewRenderer? = null

    fun login(username: String, password: String, isDone: (Boolean, String?) -> Unit) {
        firebaseClient.login(username, password, isDone)
    }

    fun reg(username: String, password: String, isDone: (Boolean, String?) -> Unit) {
        firebaseClient.reg(username, password, isDone)
    }

    // наблюдение за статусом пользователя
    fun observeUsersStatus(status:(List<Pair<String,String>>)->Unit){ //имя пользователя, статус
        firebaseClient.observeUsersStatus(status)
    }

    fun initFirebase(){
        // все клиенты будут следить за ласт ивентом
        firebaseClient.subscribeForLatestEvent(object : FirebaseClient.Listener{
            override fun onLatestEventReceived(event: DataModel) {
                //уведомляем о новом событии (что с ним сделать?)
                listener?.onLatestEventReceived(event)
                when (event.type) {
                    DataModelType.Offer ->{
                        clientWebRTC.onRemoteSessionReceived( //принять удалённый сеанс
                            SessionDescription( // создаем описание сессии
                                SessionDescription.Type.OFFER,
                                event.data.toString()
                            )
                        )
                        clientWebRTC.answer(target!!) // отвечам на звонок
                    }
                    DataModelType.Answer ->{
                        clientWebRTC.onRemoteSessionReceived(
                            SessionDescription(
                                SessionDescription.Type.ANSWER,
                                event.data.toString()
                            )
                        )
                    }
                    DataModelType.IceCandidates ->{
                        val candidate: IceCandidate? = try {
                            gson.fromJson(event.data.toString(), IceCandidate::class.java)
                        }catch (e:Exception){
                            null
                        }
                        candidate?.let { // добавляем кандидатов и передаём их
                            clientWebRTC.addIceCandidateToPeer(it)
                        }
                    }
                    DataModelType.EndCall ->{
                        listener?.endCall()
                    }
                    else -> Unit
                }
            }

        })
    }

    fun sendConnectionsRequest(target: String, isVideoCall:Boolean, success:(Boolean)->Unit) {
        firebaseClient.sendMessageToOtherClient(
            DataModel(
                type = if(isVideoCall) DataModelType.StartVideoCall else DataModelType.StartAudioCall,
                target = target
            ),
            success
        )
    }

    fun waitingResponse(success: (Boolean) -> Unit) {

    }

    fun setTarget(target: String) {
        this.target = target
    }

    fun initWebRtcClient(username: String){
        clientWebRTC.listener = this
        clientWebRTC.initializeWebrtcClient(username, object : MyPeerObserver() {

            override fun onAddStream(p0: MediaStream?) {
                super.onAddStream(p0)
                //уведомляем о новом потоке
                try {
                    p0?.videoTracks?.get(0)?.addSink(remoteView) // добавляем синхронизацию к видео
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }

            override fun onIceCandidate(p0: IceCandidate?) {
                super.onIceCandidate(p0)
                p0?.let { // если не null отправляем кандидата
                    clientWebRTC.sendIceCandidate(target!!, it)
                }
            }

            override fun onConnectionChange(newState: PeerConnection.PeerConnectionState?) {
                Log.d("MainService","onConnection:${newState}" )
                super.onConnectionChange(newState)
                if (newState == PeerConnection.PeerConnectionState.CONNECTED) {
                    // 1- смена статуса на вызове
                    changeMyStatus(UserStatus.IN_CALL)
                    // 2. очищаем ласт ивент в пользовательском разделе в бд
                    firebaseClient.clearLatestEvent()
                }
            }
        })
    }

    fun initLocalSurfaceView(view: SurfaceViewRenderer, isVideoCall: Boolean) {
        clientWebRTC.initLocalSurfaceView(view, isVideoCall)
    }

    fun initRemoteSurfaceView(view: SurfaceViewRenderer) {
        clientWebRTC.initRemoteSurfaceView(view)
        this.remoteView = view
    }

    fun startCall() {
        clientWebRTC.call(target!!)
    }

    fun endCall() {
        clientWebRTC.closeConnection()
        changeMyStatus(UserStatus.ONLINE)
    }

    fun sendEndCall() {
        onTransferEventToSocket(
            DataModel(
                type = DataModelType.EndCall,
                target = target!!
            )
        )
    }

    private fun changeMyStatus(status: UserStatus) {
        firebaseClient.changeMyStatus(status)
    }

    // авто-отключение звука
    fun toggleAudio(shouldBeMuted: Boolean) {
        clientWebRTC.toggleAudio(shouldBeMuted)
    }

    // авто-отключение видео
    fun toggleVideo(shouldBeMuted: Boolean) {
        clientWebRTC.toggleVideo(shouldBeMuted)
    }

    fun switchCamera() {
        clientWebRTC.switchCamera()
    }

    // передача ласт ивента другому участику
    override fun onTransferEventToSocket(data: DataModel) {
        firebaseClient.sendMessageToOtherClient(data) {}
    }




    fun logOff(function: () -> Unit) = firebaseClient.logOff(function)


    interface Listener{
        // функция получения ласт ивента
        fun onLatestEventReceived(dataModel: DataModel)

        fun endCall()
    }

}
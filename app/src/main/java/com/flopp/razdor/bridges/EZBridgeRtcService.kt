package com.flopp.razdor.bridges

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.flopp.razdor.model.EZModelUser
import com.flopp.razdor.network.rtc.clients.EZClientWebRtc
import com.flopp.razdor.network.rtc.listeners.EZListenerRtcTransferEvents
import com.flopp.razdor.network.rtc.models.EZModelRtcSdp
import com.flopp.razdor.services.rtc.EZIBinderRtc
import com.flopp.razdor.services.rtc.EZServiceRtc
import org.webrtc.DataChannel
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.RtpReceiver

class EZBridgeRtcService(
    activity: AppCompatActivity,
    fromUser: EZModelUser
): PeerConnection.Observer,
EZListenerRtcTransferEvents {

    companion object {
        private val TAG = EZBridgeRtcService::class.simpleName
    }

    private val mServiceConnection = object: ServiceConnection {
        override fun onServiceConnected(
            name: ComponentName?,
            service: IBinder?
        ) {
            mService = (
                service as? EZIBinderRtc
            )?.service
        }

        override fun onServiceDisconnected(
            name: ComponentName?
        ) {
            mService = null
        }
    }

    private var mService: EZServiceRtc? = null

    private val mClientRtc = EZClientWebRtc(
        activity,
        this,
        fromUser
    ).apply {
        onTransferEvents = this@EZBridgeRtcService
        isMutedAudio = false
    }

    init {
        activity.apply {
            bindService(
                Intent(
                    this,
                    EZServiceRtc::class.java
                ),
                mServiceConnection,
                Context.BIND_AUTO_CREATE
            )
        }
    }

    fun callTo(
        user: EZModelUser
    ) = mClientRtc.callTo(
        user
    )

    fun answerTo(
        user: EZModelUser
    ) = mClientRtc.answerTo(
        user
    )

    fun stop(
        activity: AppCompatActivity
    ) {
        activity.unbindService(
            mServiceConnection
        )
        activity.stopService(
            Intent(
                activity,
                EZServiceRtc::class.java
            )
        )
    }

    override fun onTransferEvent(
        data: EZModelRtcSdp
    ) {
        Log.d(TAG, "onTransferEvent: $data")
    }

    override fun onSignalingChange(
        state: PeerConnection.SignalingState?
    ) {
        Log.d(TAG, "onSignalingChange: ${state?.name}")
    }

    override fun onIceConnectionChange(
        state: PeerConnection.IceConnectionState?
    ) {
        Log.d(TAG, "onIceConnectionChange: ${state?.name}")
    }

    override fun onIceConnectionReceivingChange(
        isChanged: Boolean
    ) {
        Log.d(TAG, "onIceConnectionReceivingChange: $isChanged")
    }

    override fun onIceGatheringChange(
        state: PeerConnection.IceGatheringState?
    ) {
        Log.d(TAG, "onIceGatheringChange: ${state?.name}")
    }

    override fun onIceCandidate(
        it: IceCandidate?
    ) {
        Log.d(TAG, "onIceCandidate: $it")
    }

    override fun onIceCandidatesRemoved(
        cands: Array<out IceCandidate>?
    ) {
        Log.d(TAG, "onIceCandidatesRemoved: ${cands?.contentToString()}")
    }

    override fun onAddStream(
        stream: MediaStream?
    ) {
        Log.d(TAG, "onAddStream: ${stream?.audioTracks?.size}")
    }

    override fun onRemoveStream(
        stream: MediaStream?
    ) {
        Log.d(TAG, "onRemoveStream: ${stream?.audioTracks?.size}")
    }

    override fun onDataChannel(
        data: DataChannel?
    ) {
        Log.d(TAG, "onDataChannel: ${data?.id()}")
    }

    override fun onRenegotiationNeeded() {
        Log.d(TAG, "onRenegotiationNeeded:")
    }

    override fun onAddTrack(
        rtp: RtpReceiver?,
        streams: Array<out MediaStream>?
    ) {
        Log.d(TAG, "onAddTrack: ${streams?.size}")
    }

}
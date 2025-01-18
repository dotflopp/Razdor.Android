package com.flopp.razdor.network.rtc.clients

import android.content.Context
import com.flopp.razdor.EZApp
import com.flopp.razdor.model.EZModelUser
import com.flopp.razdor.network.rtc.EZObserverRtcSdp
import com.flopp.razdor.network.rtc.enums.EZEnumRtcSdp
import com.flopp.razdor.network.rtc.listeners.EZListenerRtcTransferEvents
import com.flopp.razdor.network.rtc.models.EZModelRtcSdp
import com.flopp.razdor.network.rtc.utils.EZUtilsRtcJson
import com.flopp.razdor.views.surface.EZViewSurfaceRtc
import org.webrtc.Camera2Enumerator
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory

class EZClientWebRtc(
    context: Context,
    observer: PeerConnection.Observer,
    private val fromUser: EZModelUser
) {

    var isMutedAudio = false
        set(v) {
            field = v
            mLocalStream?.apply {
                if (v) {
                    removeTrack(
                        mLocalAudioTrack
                    )
                    return
                }

                addTrack(
                    mLocalAudioTrack
                )
            }
        }

    var onTransferEvents: EZListenerRtcTransferEvents? = null

    init {
        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(
                context
            ).setEnableInternalTracer(
                true
            ).setFieldTrials(
                "WebRTC-H264HighProfile/Enabled/"
            ).createInitializationOptions()
        )
    }

    private val mServersIce = arrayListOf(
        PeerConnection.IceServer.builder(
            "turn:fr-turn2.xirsys.com:3478?transport=udp"
        ).setUsername(
            "b_x8c3H9otu8vC-LwmnAsPdEQnWlh_zHf54JGX8KJx2wBztiX1udhli1_MK6sxHMAAAAAGcuKjdMdWt1cw=="
        ).setPassword(
            "c8628fa0-9de3-11ef-a83d-0242ac120004"
        ).createIceServer()
    )

    private val mPeerConnectionFactory = PeerConnectionFactory
        .builder()
        .setVideoDecoderFactory(
            DefaultVideoDecoderFactory(
                EZApp.eglBaseContext
            )
        ).setVideoEncoderFactory(
            DefaultVideoEncoderFactory(
                EZApp.eglBaseContext,
                true,
                true
            )
        ).setOptions(
            PeerConnectionFactory.Options().apply {
                disableEncryption = false
                disableNetworkMonitor = false
            }
        ).createPeerConnectionFactory()

    private val mPeerConnection = mPeerConnectionFactory
        .createPeerConnection(
            mServersIce,
            observer
        )

    private val mLocalTrackId = "${fromUser.id}_track"
    private val mLocalStreamId = "${fromUser.id}_stream"

    private val mLocalAudioSource = mPeerConnectionFactory.createAudioSource(
        MediaConstraints()
    )

    private val mLocalStream = mPeerConnectionFactory.createLocalMediaStream(
        mLocalStreamId
    )

    private val mLocalAudioTrack = mPeerConnectionFactory.createAudioTrack(
        "${mLocalTrackId}_audio",
        mLocalAudioSource
    )

    private val mediaConstraint = MediaConstraints().apply {
        mandatory.apply {
            add(MediaConstraints.KeyValuePair("OfferToReceiveVideo","true"))
            add(MediaConstraints.KeyValuePair("OfferToReceiveAudio","true"))
        }
    }

    fun setupVideoSource(
        surfaceRtc: EZViewSurfaceRtc
    ) = surfaceRtc.setupVideoSource(
        mPeerConnectionFactory
    )

    fun setIsMutedVideo(
        isMuted: Boolean,
        surfaceRtc: EZViewSurfaceRtc
    ) = surfaceRtc.run {
        if (isMuted) {
            surfaceRtc.dropCamera(
                mLocalStream
            )
            return
        }

        surfaceRtc.captureCamera(
            mPeerConnectionFactory,
            mLocalTrackId,
            mLocalStream
        )
    }


    fun callTo(
        user: EZModelUser
    ) = mPeerConnection?.createOffer(
        EZObserverRtcSdp(
            user,
            EZModelRtcSdp(
                fromUser = fromUser.id,
                type = EZEnumRtcSdp.OFFER
            ),
            mPeerConnection,
            onTransferEvents
        ),
        mediaConstraint
    )

    fun answerTo(
        user: EZModelUser
    ) = mPeerConnection?.createAnswer(
        EZObserverRtcSdp(
            user,
            EZModelRtcSdp(
                fromUser = fromUser.id,
                type = EZEnumRtcSdp.ANSWER
            ),
            mPeerConnection,
            onTransferEvents
        ),
        mediaConstraint
    )

    fun addIceCandidate(
        it: IceCandidate
    ) = mPeerConnection?.addIceCandidate(it)

    fun sendIceCandidate(
        user: EZModelUser,
        it: IceCandidate
    ) {
        addIceCandidate(it)
        onTransferEvents?.onTransferEvent(
            EZModelRtcSdp(
                fromUser.id,
                user.id,
                EZEnumRtcSdp.ICE_CANDIDATES,
                EZUtilsRtcJson.jsonIceCandidate(it).toString()
            )
        )
    }

    fun release() {
        mPeerConnection?.close()
        mLocalStream?.dispose()
    }

}
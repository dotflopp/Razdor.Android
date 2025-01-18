package com.flopp.razdor.network.rtc

import com.flopp.razdor.model.EZModelUser
import com.flopp.razdor.network.rtc.listeners.EZListenerRtcTransferEvents
import com.flopp.razdor.network.rtc.models.EZModelRtcSdp
import org.webrtc.PeerConnection
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription

class EZObserverRtcSdp(
    private val toUser: EZModelUser,
    private val modelSdp: EZModelRtcSdp,
    private val mConnection: PeerConnection?,
    private val onTransferEvents: EZListenerRtcTransferEvents?
): SdpObserver {

    private var mSessionData: SessionDescription? = null

    override fun onCreateSuccess(
        desc: SessionDescription?
    ) {
        mSessionData = desc
        mConnection?.setLocalDescription(
            this,
            desc
        )
    }

    override fun onSetSuccess() {
        modelSdp.toUser = toUser.id
        modelSdp.data = mSessionData?.description

        onTransferEvents?.onTransferEvent(
            modelSdp
        )
    }

    override fun onCreateFailure(
        fail: String?
    ) = Unit

    override fun onSetFailure(
        fail: String?
    ) = Unit


}
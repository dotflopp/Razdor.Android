package com.flopp.razdor.network.rtc.utils

import org.json.JSONObject
import org.webrtc.IceCandidate

class EZUtilsRtcJson {
    companion object {
        fun jsonIceCandidate(
            it: IceCandidate
        ) = JSONObject().apply {
            put("sdp", it.sdp)
            put("sdpMid", it.sdpMid)
            put("sdpMLineIndex", it.sdpMLineIndex)
            put("serverUrl", it.serverUrl)
            put("adapterType", it.adapterType.name)
        }
    }
}
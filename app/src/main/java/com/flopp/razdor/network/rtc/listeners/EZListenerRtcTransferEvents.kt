package com.flopp.razdor.network.rtc.listeners

import com.flopp.razdor.network.rtc.models.EZModelRtcSdp

interface EZListenerRtcTransferEvents {
    fun onTransferEvent(
        data: EZModelRtcSdp
    )
}
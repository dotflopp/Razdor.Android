package com.flopp.razdor.network.rtc.models

import com.flopp.razdor.network.rtc.enums.EZEnumRtcSdp

data class EZModelRtcSdp(
    val fromUser: String? = null,
    var toUser: String? = null,
    val type: EZEnumRtcSdp,
    var data: String? = null,
    val timeStamp: Long = System.currentTimeMillis()
)
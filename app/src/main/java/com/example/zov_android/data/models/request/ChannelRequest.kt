package com.example.zov_android.data.models.request

import com.example.zov_android.domain.utils.ChannelType

data class ChannelRequest(
    val type: Int,
    val parentId: Long,
    val name: String,
    val position: Int,
    val bitrate: Int,
    val userLimit: Int
)

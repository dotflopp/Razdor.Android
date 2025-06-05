package com.example.zov_android.data.models.request

import com.example.zov_android.domain.utils.ChannelType

data class ChannelRequest(
    val name: String,
    val type: ChannelType,
    val parentId: Long
)

package com.example.zov_android.data.models.response

import com.example.zov_android.domain.utils.ChannelType

data class ChannelResponse(
    val id: String,
    val communityId: String,
    val type: ChannelType,
    val parentId: String,
    val name: String,
    val isSyncing: Boolean,
    val overwrites: List<Overwrite>
)

data class Overwrite(
    val targetId: String,
    val targetType: String,
    val permissions: Permissions
)

data class Permissions(
    val allow: Int,
    val deny: Int
)
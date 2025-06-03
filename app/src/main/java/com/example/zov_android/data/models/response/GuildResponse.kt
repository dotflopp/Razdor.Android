package com.example.zov_android.data.models.response

import com.example.zov_android.domain.utils.DefaultNotificationPolicy

data class GuildResponse(
    val id: String,
    val ownerId: String,
    val name: String,
    val avatar: String?,
    val description: String?,
    val defaultNotificationPolicy: DefaultNotificationPolicy,
    val roles: List<Role>
)
data class Role(
    val id: String,
    val priority: Int,
    val name: String,
    val permissions: Int,
    val isMentioned: Boolean
)

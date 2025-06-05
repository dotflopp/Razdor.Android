package com.example.zov_android.data.models.response

import com.example.zov_android.domain.utils.UserCommunicationDisplayedStatus
import java.io.Serializable
import java.time.Instant

data class MembersGuildResponse(
    val userId: String,
    val communityId: String,
    val identityName: String,
    val status: UserCommunicationDisplayedStatus,
    val nickname: String,
    val avatar: String?,
    val joiningDate: String,
    val voiceState: VoiceState,
    val roleIds: List<String>
): Serializable

data class VoiceState(
    val channelId: Long,
    val isDeafened: Boolean,
    val isMuted: Boolean,
    val isSelfDeafened: Boolean,
    val isSelfMuted: Boolean
)

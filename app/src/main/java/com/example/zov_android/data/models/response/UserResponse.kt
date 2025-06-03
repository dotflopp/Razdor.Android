package com.example.zov_android.data.models.response

import com.example.zov_android.domain.utils.UserCommunicationDisplayedStatus
import com.example.zov_android.domain.utils.UserCommunicationSelectedStatus
import java.io.Serializable
import java.time.Instant

data class UserResponse(
    val id: String,
    val email: String,
    val identityName: String,
    val nickname: String,
    val avatar: String?,
    val credentialChangeDate: Instant,
    val selectedStatus: UserCommunicationSelectedStatus,
    val status: UserCommunicationDisplayedStatus,
    val description: String?
): Serializable
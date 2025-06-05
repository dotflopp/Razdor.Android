package com.example.zov_android.data.models.response

import java.time.Instant

data class InvitesResponse (
    val id: String,
    val creatorId: String,
    val communityId: String,
    val expiresAt: Instant?,
    val createdAt: String,
    val usesCount: Int
)
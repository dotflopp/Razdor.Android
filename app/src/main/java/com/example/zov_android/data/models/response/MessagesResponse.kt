package com.example.zov_android.data.models.response

data class MessagesResponse(
    val id: String,
    val userId: String,
    val channelId: String,
    val text: String?,
    val createdAt: String,
    val reference: Reference?,
    val embed: Embed?,
    val isPinned: Boolean,
    val editedAt: String?,
    val attachments: List<Attachment>,
    val mentionedEveryone: Boolean,
    val mentionedUsers: List<MentionedUser>,
    val mentionedChannels: List<MentionedChannel>,
    val mentionedRoles: List<MentionedRole>
)

data class Reference(
    val channelId: String?,
    val messageId: String?
)

data class Embed(
    val title: String?,
    val description: String?,
    val fields: List<EmbedField>,
    val footer: EmbedFooter?
)

data class EmbedField(
    val isInline: Boolean,
    val title: String?,
    val description: String?
)

data class EmbedFooter(
    val imageUrl: String?,
    val title: String?,
    val timestamp: String?
)

data class Attachment(
    val id: String,
    val sourceUrl: String,
    val mediaType: String,
    val size: Int
)

data class MentionedUser(val userId: String)
data class MentionedChannel(val channelId: String)
data class MentionedRole(val roleId: String)
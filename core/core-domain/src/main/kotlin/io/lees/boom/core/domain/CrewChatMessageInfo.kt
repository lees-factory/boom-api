package io.lees.boom.core.domain

import java.time.LocalDateTime

data class CrewChatMessageInfo(
    val messageId: Long,
    val memberId: Long,
    val memberName: String,
    val memberProfileImage: String?,
    val content: String,
    val createdAt: LocalDateTime,
)

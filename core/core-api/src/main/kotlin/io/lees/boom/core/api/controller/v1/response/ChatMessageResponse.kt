package io.lees.boom.core.api.controller.v1.response

import io.lees.boom.core.domain.CrewChatMessage
import java.time.LocalDateTime

data class ChatMessageResponse(
    val messageId: Long,
    val memberId: Long,
    val memberName: String,
    val memberProfileImage: String?,
    val content: String,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(message: CrewChatMessage): ChatMessageResponse =
            ChatMessageResponse(
                messageId = message.id!!,
                memberId = message.memberId,
                memberName = message.memberName,
                memberProfileImage = message.memberProfileImage,
                content = message.content,
                createdAt = message.createdAt!!,
            )
    }
}

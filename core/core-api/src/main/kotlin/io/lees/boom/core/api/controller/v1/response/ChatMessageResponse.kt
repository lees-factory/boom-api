package io.lees.boom.core.api.controller.v1.response

import io.lees.boom.core.domain.CrewChatMessageInfo
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
        fun from(info: CrewChatMessageInfo): ChatMessageResponse =
            ChatMessageResponse(
                messageId = info.messageId,
                memberId = info.memberId,
                memberName = info.memberName,
                memberProfileImage = info.memberProfileImage,
                content = info.content,
                createdAt = info.createdAt,
            )
    }
}

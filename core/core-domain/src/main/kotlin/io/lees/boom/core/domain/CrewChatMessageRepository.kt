package io.lees.boom.core.domain

import java.time.LocalDateTime

interface CrewChatMessageRepository {
    fun save(message: CrewChatMessage): CrewChatMessage

    fun findById(messageId: Long): CrewChatMessage?

    fun findMessagesWithInfo(
        crewId: Long,
        cursor: Long?,
        size: Int,
    ): List<CrewChatMessageInfo>

    fun countRecentMessages(
        crewId: Long,
        memberId: Long,
        since: LocalDateTime,
    ): Long

    fun softDelete(messageId: Long)
}

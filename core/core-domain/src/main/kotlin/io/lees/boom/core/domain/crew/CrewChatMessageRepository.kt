package io.lees.boom.core.domain.crew

import java.time.LocalDateTime

interface CrewChatMessageRepository {
    fun save(message: CrewChatMessage): CrewChatMessage

    fun findById(messageId: Long): CrewChatMessage?

    fun findMessages(
        crewId: Long,
        cursor: Long?,
        size: Int,
        blockedMemberIds: List<Long> = emptyList(),
    ): List<CrewChatMessage>

    fun countRecentMessages(
        crewId: Long,
        memberId: Long,
        since: LocalDateTime,
    ): Long

    fun softDelete(messageId: Long)

    fun softDeleteByMemberId(memberId: Long)
}

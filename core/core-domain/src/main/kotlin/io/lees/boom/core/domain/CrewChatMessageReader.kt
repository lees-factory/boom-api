package io.lees.boom.core.domain

import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class CrewChatMessageReader(
    private val crewChatMessageRepository: CrewChatMessageRepository,
) {
    fun readById(messageId: Long): CrewChatMessage? = crewChatMessageRepository.findById(messageId)

    fun readMessages(
        crewId: Long,
        cursor: Long?,
        size: Int,
    ): List<CrewChatMessageInfo> = crewChatMessageRepository.findMessagesWithInfo(crewId, cursor, size)

    fun countRecentMessages(
        crewId: Long,
        memberId: Long,
        since: LocalDateTime,
    ): Long = crewChatMessageRepository.countRecentMessages(crewId, memberId, since)
}

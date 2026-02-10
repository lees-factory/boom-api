package io.lees.boom.core.domain

import org.springframework.stereotype.Component

@Component
class CrewChatMessageAppender(
    private val crewChatMessageRepository: CrewChatMessageRepository,
) {
    fun append(message: CrewChatMessage): CrewChatMessage = crewChatMessageRepository.save(message)

    fun softDelete(messageId: Long) = crewChatMessageRepository.softDelete(messageId)
}

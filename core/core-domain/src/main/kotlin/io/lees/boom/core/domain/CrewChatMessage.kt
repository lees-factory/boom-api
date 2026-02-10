package io.lees.boom.core.domain

import java.time.LocalDateTime

data class CrewChatMessage(
    val id: Long? = null,
    val crewId: Long,
    val memberId: Long,
    val content: String,
    val createdAt: LocalDateTime? = null,
) {
    companion object {
        fun create(
            crewId: Long,
            memberId: Long,
            content: String,
        ): CrewChatMessage =
            CrewChatMessage(
                crewId = crewId,
                memberId = memberId,
                content = content,
            )
    }
}

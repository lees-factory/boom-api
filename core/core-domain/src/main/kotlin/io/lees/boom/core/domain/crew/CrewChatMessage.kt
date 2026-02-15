package io.lees.boom.core.domain.crew

import java.time.LocalDateTime

data class CrewChatMessage(
    val id: Long? = null,
    val crewId: Long,
    val memberId: Long,
    val memberName: String,
    val memberProfileImage: String?,
    val content: String,
    val createdAt: LocalDateTime? = null,
) {
    companion object {
        fun create(
            crewId: Long,
            memberId: Long,
            memberName: String,
            memberProfileImage: String?,
            content: String,
        ): CrewChatMessage =
            CrewChatMessage(
                crewId = crewId,
                memberId = memberId,
                memberName = memberName,
                memberProfileImage = memberProfileImage,
                content = content,
            )
    }
}

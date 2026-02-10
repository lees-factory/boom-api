package io.lees.boom.storage.db.core

import io.lees.boom.core.domain.CrewChatMessage
import io.lees.boom.core.domain.CrewChatMessageInfo
import io.lees.boom.core.domain.CrewChatMessageRepository
import jakarta.transaction.Transactional
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
internal class CrewChatMessageCoreRepository(
    private val crewChatMessageJpaRepository: CrewChatMessageJpaRepository,
) : CrewChatMessageRepository {
    override fun save(message: CrewChatMessage): CrewChatMessage {
        val entity = message.toEntity()
        return crewChatMessageJpaRepository.save(entity).toDomain()
    }

    override fun findById(messageId: Long): CrewChatMessage? =
        crewChatMessageJpaRepository.findByIdOrNull(messageId)?.toDomain()

    override fun findMessagesWithInfo(
        crewId: Long,
        cursor: Long?,
        size: Int,
    ): List<CrewChatMessageInfo> {
        val pageable = PageRequest.of(0, size)
        return crewChatMessageJpaRepository
            .findMessagesWithInfo(crewId, cursor, pageable)
            .map { it.toDomain() }
    }

    override fun countRecentMessages(
        crewId: Long,
        memberId: Long,
        since: LocalDateTime,
    ): Long = crewChatMessageJpaRepository.countRecentMessages(crewId, memberId, since)

    @Transactional
    override fun softDelete(messageId: Long) {
        crewChatMessageJpaRepository.softDeleteById(messageId)
    }

    private fun CrewChatMessage.toEntity() =
        CrewChatMessageEntity(
            crewId = this.crewId,
            memberId = this.memberId,
            content = this.content,
        )

    private fun CrewChatMessageEntity.toDomain() =
        CrewChatMessage(
            id = this.id,
            crewId = this.crewId,
            memberId = this.memberId,
            content = this.content,
            createdAt = this.createdAt,
        )

    private fun ChatMessageInfoProjection.toDomain() =
        CrewChatMessageInfo(
            messageId = this.messageId,
            memberId = this.memberId,
            memberName = this.memberName,
            memberProfileImage = this.memberProfileImage,
            content = this.content,
            createdAt = this.createdAt,
        )
}

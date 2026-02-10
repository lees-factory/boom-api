package io.lees.boom.storage.db.core

import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDateTime

@Entity
@Table(name = "crew_chat_message")
@SQLRestriction("deleted_at IS NULL")
class CrewChatMessageEntity(
    val crewId: Long,
    val memberId: Long,
    val content: String,
    var deletedAt: LocalDateTime? = null,
) : BaseEntity()

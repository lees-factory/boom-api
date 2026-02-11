package io.lees.boom.storage.db.core

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface ChatMessageInfoProjection {
    val messageId: Long
    val memberId: Long
    val memberName: String
    val memberProfileImage: String?
    val content: String
    val createdAt: LocalDateTime
}

interface CrewChatMessageJpaRepository : JpaRepository<CrewChatMessageEntity, Long> {
    @Query(
        """
        SELECT cm.id as messageId, cm.memberId as memberId,
               m.name as memberName, m.profileImage as memberProfileImage,
               cm.content as content, cm.createdAt as createdAt
        FROM CrewChatMessageEntity cm
        JOIN MemberEntity m ON cm.memberId = m.id
        WHERE cm.crewId = :crewId AND (:cursor IS NULL OR cm.id < :cursor)
              AND (:blockedMemberIds IS NULL OR cm.memberId NOT IN :blockedMemberIds)
        ORDER BY cm.id DESC
        """,
    )
    fun findMessagesWithInfo(
        @Param("crewId") crewId: Long,
        @Param("cursor") cursor: Long?,
        @Param("blockedMemberIds") blockedMemberIds: List<Long>?,
        pageable: Pageable,
    ): List<ChatMessageInfoProjection>

    @Query(
        """
        SELECT COUNT(cm) FROM CrewChatMessageEntity cm
        WHERE cm.crewId = :crewId AND cm.memberId = :memberId AND cm.createdAt >= :since
        """,
    )
    fun countRecentMessages(
        @Param("crewId") crewId: Long,
        @Param("memberId") memberId: Long,
        @Param("since") since: LocalDateTime,
    ): Long

    @Modifying
    @Query("UPDATE CrewChatMessageEntity cm SET cm.deletedAt = CURRENT_TIMESTAMP WHERE cm.id = :id")
    fun softDeleteById(
        @Param("id") id: Long,
    )
}

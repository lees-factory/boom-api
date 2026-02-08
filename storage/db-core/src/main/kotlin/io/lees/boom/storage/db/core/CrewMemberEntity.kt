package io.lees.boom.storage.db.core

import io.lees.boom.core.enums.CrewRole
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDateTime

@Entity
@Table(name = "crew_member")
@SQLRestriction("deleted_at IS NULL")
class CrewMemberEntity(
    val crewId: Long, // 연관관계 매핑 없이 ID만 저장
    val memberId: Long, // 연관관계 매핑 없이 ID만 저장
    @Enumerated(EnumType.STRING)
    val role: CrewRole,
    var deletedAt: LocalDateTime? = null,
) : BaseEntity()

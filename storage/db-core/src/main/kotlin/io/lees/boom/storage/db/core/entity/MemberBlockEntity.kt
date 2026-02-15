package io.lees.boom.storage.db.core.entity

import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "member_block")
class MemberBlockEntity(
    val blockerId: Long,
    val blockedId: Long,
) : BaseEntity()

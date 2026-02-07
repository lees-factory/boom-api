package io.lees.boom.core.domain

import io.lees.boom.core.enums.BadgeType
import java.time.LocalDateTime

data class MemberBadge(
    val id: Long? = null,
    val memberId: Long,
    val badgeType: BadgeType,
    val acquiredAt: LocalDateTime,
    val notified: Boolean = false,
) {
    companion object {
        fun create(
            memberId: Long,
            badgeType: BadgeType,
        ): MemberBadge =
            MemberBadge(
                memberId = memberId,
                badgeType = badgeType,
                acquiredAt = LocalDateTime.now(),
            )
    }
}

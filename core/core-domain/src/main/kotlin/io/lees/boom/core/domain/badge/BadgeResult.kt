package io.lees.boom.core.domain.badge

import io.lees.boom.core.enums.BadgeType
import java.time.LocalDateTime

data class BadgeResult(
    val badgeType: BadgeType,
    val isEarned: Boolean,
    val isNew: Boolean,
    val acquiredAt: LocalDateTime?,
)

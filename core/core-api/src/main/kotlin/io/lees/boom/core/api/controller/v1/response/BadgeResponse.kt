package io.lees.boom.core.api.controller.v1.response

import com.fasterxml.jackson.annotation.JsonProperty
import io.lees.boom.core.domain.badge.BadgeResult
import java.time.LocalDateTime

data class BadgeResponse(
    val badgeType: String,
    val category: String,
    val title: String,
    val emoji: String,
    val description: String,
    @get:JsonProperty("isEarned")
    val isEarned: Boolean,
    @get:JsonProperty("isNew")
    val isNew: Boolean,
    val acquiredAt: LocalDateTime?,
) {
    companion object {
        fun from(result: BadgeResult): BadgeResponse =
            BadgeResponse(
                badgeType = result.badgeType.name,
                category = result.badgeType.category.name,
                title = result.badgeType.title,
                emoji = result.badgeType.emoji,
                description = result.badgeType.description,
                isEarned = result.isEarned,
                isNew = result.isNew,
                acquiredAt = result.acquiredAt,
            )
    }
}

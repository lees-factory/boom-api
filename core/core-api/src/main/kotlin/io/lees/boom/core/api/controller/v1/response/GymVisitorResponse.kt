package io.lees.boom.core.api.controller.v1.response

import io.lees.boom.core.domain.GymVisitor
import java.time.LocalDateTime

data class GymVisitorResponse(
    val memberId: Long,
    val memberName: String,
    val memberProfileImage: String?,
    val admittedAt: LocalDateTime,
) {
    companion object {
        fun of(visitor: GymVisitor): GymVisitorResponse =
            GymVisitorResponse(
                memberId = visitor.memberId,
                memberName = visitor.memberName,
                memberProfileImage = visitor.memberProfileImage,
                admittedAt = visitor.admittedAt,
            )
    }
}
package io.lees.boom.core.api.controller.v1.response

import io.lees.boom.core.domain.Member
import io.lees.boom.core.enums.MemberRole

data class MemberResponse(
    val id: Long,
    val name: String,
    val email: String,
    val role: MemberRole,
) {
    companion object {
        fun from(member: Member): MemberResponse =
            MemberResponse(
                id = member.id ?: 0L,
                name = member.name,
                email = member.email,
                role = member.role,
            )
    }
}

package io.lees.boom.core.api.controller.v1.response

import io.lees.boom.core.domain.Member

data class MemberResponse(
    val id: Long,
    val name: String,
    val email: String?,
    val profileImage: String?,
) {
    companion object {
        fun of(member: Member): MemberResponse =
            MemberResponse(
                id = member.id!!,
                name = member.name,
                email = member.email,
                profileImage = member.profileImage,
            )
    }
}

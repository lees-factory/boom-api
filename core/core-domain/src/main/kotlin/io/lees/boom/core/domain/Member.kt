package io.lees.boom.core.domain

import io.lees.boom.core.enums.MemberRole
import io.lees.boom.core.enums.SocialProvider

data class Member(
    val id: Long? = null,
    val name: String,
    val email: String,
    val role: MemberRole,
    val socialInfo: SocialInfo,
) {
    companion object {
        fun register(
            name: String,
            email: String,
            role: MemberRole,
            provider: SocialProvider,
            socialId: String,
        ): Member =
            Member(
                name = name,
                email = email,
                role = role,
                socialInfo = SocialInfo(provider, socialId),
            )
    }
}

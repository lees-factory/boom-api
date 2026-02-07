package io.lees.boom.core.domain

import io.lees.boom.core.enums.MemberRole
import io.lees.boom.core.enums.SocialProvider

data class Member(
    val id: Long? = null,
    val name: String,
    val email: String?,
    val profileImage: String?,
    val role: MemberRole,
    val socialInfo: SocialInfo,
    val activityScore: Int = 0,
) {
    companion object {
        fun register(
            name: String,
            email: String,
            profileImage: String?,
            role: MemberRole,
            provider: SocialProvider,
            socialId: String,
        ): Member =
            Member(
                name = name,
                email = email,
                profileImage = profileImage,
                role = role,
                socialInfo = SocialInfo(provider, socialId),
            )
    }
}

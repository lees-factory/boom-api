package io.lees.boom.core.domain

import io.lees.boom.core.enums.MemberRole
import io.lees.boom.core.enums.SocialProvider

data class Member(
    val id: Long? = null,
    val name: String,
    val email: String?, // 이메일이 없는 경우 빈 문자열 또는 null 처리 고려
    val profileImage: String?, // [추가]
    val role: MemberRole,
    val socialInfo: SocialInfo,
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

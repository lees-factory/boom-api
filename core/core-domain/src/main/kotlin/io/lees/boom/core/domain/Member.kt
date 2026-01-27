package io.lees.boom.core.domain

import io.lees.boom.core.enums.SocialProvider

data class Member(
    val id: Long? = null,
    val name: String,
    val email: String,
    val socialInfo: SocialInfo,
) {
    companion object {
        fun register(name: String, email: String, provider: SocialProvider, socialId: String): Member {
            return Member(
                name = name,
                email = email,
                socialInfo = SocialInfo(provider, socialId),
            )
        }
    }
}

package io.lees.boom.core.domain

import io.lees.boom.core.enums.SocialProvider
import org.springframework.stereotype.Service

@Service
class SocialLoginService(
    private val socialProfileReader: SocialProfileReader,
    private val memberFinder: MemberFinder,
    private val memberAppender: MemberAppender,
) {
    suspend fun login(provider: SocialProvider, token: String): Member {

        val profile = socialProfileReader.read(provider, token)

        val member = memberFinder.findBySocialInfo(profile.provider, profile.socialId)

        return member ?: run {
            val newMember = Member.register(
                name = profile.name,
                email = profile.email,
                provider = profile.provider,
                socialId = profile.socialId,
            )
            memberAppender.append(newMember)
        }
    }
}
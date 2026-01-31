package io.lees.boom.core.domain

import io.lees.boom.core.enums.SocialProvider
import org.springframework.stereotype.Component

@Component
class MemberFinder(
    private val memberRepository: MemberRepository,
) {
    suspend fun findBySocialInfo(
        provider: SocialProvider,
        socialId: String,
    ): Member? = memberRepository.findBySocialInfo(provider, socialId)
}

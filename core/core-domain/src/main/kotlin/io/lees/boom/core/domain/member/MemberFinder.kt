package io.lees.boom.core.domain.member

import io.lees.boom.core.enums.SocialProvider
import org.springframework.stereotype.Component

@Component
class MemberFinder(
    private val memberRepository: MemberRepository,
) {
    fun findBySocialInfo(
        provider: SocialProvider,
        socialId: String,
    ): Member? = memberRepository.findBySocialInfo(provider, socialId)

    fun findById(memberId: Long): Member? = memberRepository.findById(memberId)
}

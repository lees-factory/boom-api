package io.lees.boom.core.domain

import org.springframework.stereotype.Component

@Component
class ActivityScoreUpdater(
    private val memberRepository: MemberRepository,
) {
    companion object {
        const val SCHEDULE_PARTICIPATE_SCORE = 10
        const val GYM_VISIT_SCORE = 5
        // TODO: 피드 기능 확장 후 반영
        // const val FEED_POST_SCORE = 3
        // const val COMMENT_LIKE_SCORE = 1
    }

    fun addScheduleParticipateScore(memberId: Long) {
        memberRepository.incrementActivityScore(memberId, SCHEDULE_PARTICIPATE_SCORE)
    }

    fun addGymVisitScore(memberId: Long) {
        memberRepository.incrementActivityScore(memberId, GYM_VISIT_SCORE)
    }
}

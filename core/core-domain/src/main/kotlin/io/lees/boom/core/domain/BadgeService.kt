package io.lees.boom.core.domain

import io.lees.boom.core.enums.BadgeType
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class BadgeService(
    private val memberBadgeRepository: MemberBadgeRepository,
    private val badgeEvaluator: BadgeEvaluator,
) {
    /**
     * 내 뱃지 조회 (on-demand 계산 + 저장)
     * 1. 기획득 뱃지 조회
     * 2. 미획득 뱃지 조건 평가
     * 3. 신규 획득 뱃지 저장
     * 4. 전체 뱃지 결과 반환
     */
    @Transactional
    fun getMyBadges(memberId: Long): List<BadgeResult> {
        val existingBadges = memberBadgeRepository.findByMemberId(memberId)
        val earnedTypes = existingBadges.map { it.badgeType }.toSet()

        val unearnedTypes = BadgeType.entries.filter { it !in earnedTypes }
        val newlyEarnedTypes = badgeEvaluator.evaluate(memberId, unearnedTypes)

        val newBadges =
            if (newlyEarnedTypes.isNotEmpty()) {
                val badges = newlyEarnedTypes.map { MemberBadge.create(memberId, it) }
                memberBadgeRepository.saveAll(badges)
            } else {
                emptyList()
            }

        val allBadges = existingBadges + newBadges
        val allBadgeMap = allBadges.associateBy { it.badgeType }
        val newBadgeTypes = newBadges.map { it.badgeType }.toSet()

        return BadgeType.entries.map { type ->
            val badge = allBadgeMap[type]
            BadgeResult(
                badgeType = type,
                isEarned = badge != null,
                isNew = type in newBadgeTypes,
                acquiredAt = badge?.acquiredAt,
            )
        }
    }

    /**
     * 타 유저 뱃지 조회 (계산 없이 저장된 것만 조회)
     */
    fun getMemberBadges(memberId: Long): List<BadgeResult> {
        val existingBadges = memberBadgeRepository.findByMemberId(memberId)
        val badgeMap = existingBadges.associateBy { it.badgeType }

        return BadgeType.entries.map { type ->
            val badge = badgeMap[type]
            BadgeResult(
                badgeType = type,
                isEarned = badge != null,
                isNew = false,
                acquiredAt = badge?.acquiredAt,
            )
        }
    }
}

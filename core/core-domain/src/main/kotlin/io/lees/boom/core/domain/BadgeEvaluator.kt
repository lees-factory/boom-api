package io.lees.boom.core.domain

import io.lees.boom.core.enums.BadgeCategory
import io.lees.boom.core.enums.BadgeType
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class BadgeEvaluator(
    private val badgeConditionRepository: BadgeConditionRepository,
) {
    fun evaluate(
        memberId: Long,
        unearnedBadges: List<BadgeType>,
    ): List<BadgeType> {
        if (unearnedBadges.isEmpty()) return emptyList()

        val unearnedByCategory = unearnedBadges.groupBy { it.category }
        val newlyEarned = mutableListOf<BadgeType>()

        unearnedByCategory.forEach { (category, badges) ->
            newlyEarned.addAll(evaluateCategory(memberId, category, badges))
        }

        return newlyEarned
    }

    private fun evaluateCategory(
        memberId: Long,
        category: BadgeCategory,
        badges: List<BadgeType>,
    ): List<BadgeType> =
        when (category) {
            BadgeCategory.ATTENDANCE -> evaluateAttendance(memberId, badges)
            BadgeCategory.STREAK -> evaluateStreak(memberId, badges)
            BadgeCategory.WEEKLY -> evaluateWeekly(memberId, badges)
            BadgeCategory.TIME -> evaluateTime(memberId, badges)
            BadgeCategory.VISIT_PATTERN -> evaluateVisitPattern(memberId, badges)
        }

    private fun evaluateAttendance(
        memberId: Long,
        badges: List<BadgeType>,
    ): List<BadgeType> {
        val count = badgeConditionRepository.countAdmissions(memberId)
        return badges.filter { count >= it.threshold }
    }

    private fun evaluateStreak(
        memberId: Long,
        badges: List<BadgeType>,
    ): List<BadgeType> {
        val dates = badgeConditionRepository.findDistinctVisitDates(memberId)
        val maxStreak = calculateMaxStreak(dates)
        return badges.filter { maxStreak >= it.threshold }
    }

    private fun evaluateWeekly(
        memberId: Long,
        badges: List<BadgeType>,
    ): List<BadgeType> {
        val maxWeeklyDays = badgeConditionRepository.findMaxWeeklyVisitDays(memberId)
        return badges.filter { maxWeeklyDays >= it.threshold }
    }

    private fun evaluateTime(
        memberId: Long,
        badges: List<BadgeType>,
    ): List<BadgeType> {
        val earned = mutableListOf<BadgeType>()
        for (badge in badges) {
            when (badge) {
                BadgeType.EARLY -> if (badgeConditionRepository.existsEarlyVisit(memberId)) earned.add(badge)
                BadgeType.NIGHT -> if (badgeConditionRepository.existsNightVisit(memberId)) earned.add(badge)
                else -> {}
            }
        }
        return earned
    }

    private fun evaluateVisitPattern(
        memberId: Long,
        badges: List<BadgeType>,
    ): List<BadgeType> {
        val earned = mutableListOf<BadgeType>()
        for (badge in badges) {
            when (badge) {
                BadgeType.OPENER -> if (badgeConditionRepository.existsOpenerVisit(memberId)) earned.add(badge)
                BadgeType.WANDERER -> if (badgeConditionRepository.existsWandererVisit(memberId)) earned.add(badge)
                else -> {}
            }
        }
        return earned
    }

    companion object {
        fun calculateMaxStreak(sortedDates: List<LocalDate>): Int {
            if (sortedDates.isEmpty()) return 0

            val sorted = sortedDates.sorted()
            var maxStreak = 1
            var currentStreak = 1

            for (i in 1 until sorted.size) {
                if (sorted[i] == sorted[i - 1].plusDays(1)) {
                    currentStreak++
                    maxStreak = maxOf(maxStreak, currentStreak)
                } else if (sorted[i] != sorted[i - 1]) {
                    currentStreak = 1
                }
            }

            return maxStreak
        }
    }
}

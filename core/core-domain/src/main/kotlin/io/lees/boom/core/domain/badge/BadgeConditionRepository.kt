package io.lees.boom.core.domain.badge

import java.time.LocalDate

interface BadgeConditionRepository {
    fun countAdmissions(memberId: Long): Long

    fun findDistinctVisitDates(memberId: Long): List<LocalDate>

    fun findMaxWeeklyVisitDays(memberId: Long): Int

    fun existsEarlyVisit(memberId: Long): Boolean

    fun existsNightVisit(memberId: Long): Boolean

    fun existsOpenerVisit(memberId: Long): Boolean

    fun existsWandererVisit(memberId: Long): Boolean
}

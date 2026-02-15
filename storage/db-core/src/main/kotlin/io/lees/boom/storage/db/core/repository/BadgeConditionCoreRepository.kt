package io.lees.boom.storage.db.core.repository

import io.lees.boom.core.domain.badge.BadgeConditionRepository
import io.lees.boom.core.enums.VisitStatus
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
internal class BadgeConditionCoreRepository(
    private val gymVisitJpaRepository: GymVisitJpaRepository,
) : BadgeConditionRepository {
    override fun countAdmissions(memberId: Long): Long =
        gymVisitJpaRepository.countByMemberIdAndStatus(memberId, VisitStatus.ADMISSION)

    override fun findDistinctVisitDates(memberId: Long): List<LocalDate> =
        gymVisitJpaRepository.findDistinctAdmittedDates(memberId, VisitStatus.ADMISSION)

    override fun findMaxWeeklyVisitDays(memberId: Long): Int =
        gymVisitJpaRepository.findMaxWeeklyVisitDaysByMemberId(memberId)

    override fun existsEarlyVisit(memberId: Long): Boolean = gymVisitJpaRepository.existsEarlyVisitByMemberId(memberId)

    override fun existsNightVisit(memberId: Long): Boolean = gymVisitJpaRepository.existsNightVisitByMemberId(memberId)

    override fun existsOpenerVisit(memberId: Long): Boolean =
        gymVisitJpaRepository.existsOpenerVisitByMemberId(memberId)

    override fun existsWandererVisit(memberId: Long): Boolean =
        gymVisitJpaRepository.existsWandererVisitByMemberId(memberId)
}

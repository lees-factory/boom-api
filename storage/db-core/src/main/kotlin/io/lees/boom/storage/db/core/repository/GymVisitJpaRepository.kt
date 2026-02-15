package io.lees.boom.storage.db.core.repository

import io.lees.boom.core.enums.VisitStatus
import io.lees.boom.storage.db.core.entity.GymVisitEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate

internal interface GymVisitJpaRepository : JpaRepository<GymVisitEntity, Long> {
    fun countByMemberIdAndStatus(
        memberId: Long,
        status: VisitStatus,
    ): Long

    @Query(
        """
        SELECT DISTINCT CAST(g.admittedAt AS LocalDate)
        FROM GymVisitEntity g
        WHERE g.memberId = :memberId AND g.status = :status
        ORDER BY 1
        """,
    )
    fun findDistinctAdmittedDates(
        memberId: Long,
        status: VisitStatus,
    ): List<LocalDate>

    @Query(
        """
        SELECT CASE WHEN COUNT(g) > 0 THEN true ELSE false END
        FROM GymVisitEntity g
        WHERE g.memberId = :memberId AND g.status = 'ADMISSION'
          AND extract(hour from g.admittedAt) >= 5
          AND extract(hour from g.admittedAt) < 9
        """,
    )
    fun existsEarlyVisitByMemberId(memberId: Long): Boolean

    @Query(
        """
        SELECT CASE WHEN COUNT(g) > 0 THEN true ELSE false END
        FROM GymVisitEntity g
        WHERE g.memberId = :memberId AND g.status = 'ADMISSION'
          AND (extract(hour from g.admittedAt) >= 22
               OR extract(hour from g.admittedAt) < 2)
        """,
    )
    fun existsNightVisitByMemberId(memberId: Long): Boolean

    @Query(
        """
        SELECT CASE WHEN COUNT(g) > 0 THEN true ELSE false END
        FROM GymVisitEntity g
        WHERE g.memberId = :memberId AND g.status = 'ADMISSION'
          AND extract(hour from g.admittedAt) = 10
          AND extract(minute from g.admittedAt) < 30
        """,
    )
    fun existsOpenerVisitByMemberId(memberId: Long): Boolean

    @Query(
        nativeQuery = true,
        value = """
            SELECT COALESCE(MAX(day_count), 0)
            FROM (
                SELECT COUNT(DISTINCT CAST(admitted_at AS DATE)) AS day_count
                FROM gym_visit
                WHERE member_id = :memberId AND status = 'ADMISSION'
                GROUP BY EXTRACT(ISOYEAR FROM admitted_at), EXTRACT(WEEK FROM admitted_at)
            ) weekly
        """,
    )
    fun findMaxWeeklyVisitDaysByMemberId(memberId: Long): Int

    @Query(
        nativeQuery = true,
        value = """
            SELECT CASE WHEN EXISTS (
                SELECT 1 FROM gym_visit v1
                JOIN gym_visit v2 ON v1.member_id = v2.member_id
                    AND v1.id <> v2.id
                    AND v1.gym_id <> v2.gym_id
                    AND v2.admitted_at BETWEEN v1.admitted_at AND v1.admitted_at + INTERVAL '48 hours'
                WHERE v1.member_id = :memberId
                AND v1.status = 'ADMISSION'
                AND v2.status = 'ADMISSION'
            ) THEN true ELSE false END
        """,
    )
    fun existsWandererVisitByMemberId(memberId: Long): Boolean
}

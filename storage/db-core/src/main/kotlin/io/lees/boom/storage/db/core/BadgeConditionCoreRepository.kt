package io.lees.boom.storage.db.core

import io.lees.boom.core.domain.BadgeConditionRepository
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository
import java.sql.Date
import java.time.LocalDate

@Repository
internal class BadgeConditionCoreRepository(
    private val entityManager: EntityManager,
) : BadgeConditionRepository {
    /**
     * ADMISSION 상태의 방문 횟수 (출석 누적)
     */
    override fun countAdmissions(memberId: Long): Long {
        val query =
            entityManager.createNativeQuery(
                """
                SELECT COUNT(*)
                FROM gym_visit
                WHERE member_id = :memberId AND status = 'ADMISSION'
                """.trimIndent(),
            )
        query.setParameter("memberId", memberId)
        return (query.singleResult as Number).toLong()
    }

    /**
     * 방문일 목록 (streak 계산용, 인메모리에서 연속일 계산)
     */
    override fun findDistinctVisitDates(memberId: Long): List<LocalDate> {
        val query =
            entityManager.createNativeQuery(
                """
                SELECT DISTINCT CAST(admitted_at AS DATE)
                FROM gym_visit
                WHERE member_id = :memberId AND status = 'ADMISSION'
                ORDER BY 1
                """.trimIndent(),
            )
        query.setParameter("memberId", memberId)

        @Suppress("UNCHECKED_CAST")
        val results = query.resultList as List<Any>
        return results.map { result ->
            when (result) {
                is Date -> result.toLocalDate()
                is LocalDate -> result
                else -> throw IllegalStateException("Unexpected date type: ${result::class}")
            }
        }
    }

    /**
     * 역대 최대 주간 출석일 수 (ISO 주 기준)
     */
    override fun findMaxWeeklyVisitDays(memberId: Long): Int {
        val query =
            entityManager.createNativeQuery(
                """
                SELECT COALESCE(MAX(day_count), 0)
                FROM (
                    SELECT COUNT(DISTINCT CAST(admitted_at AS DATE)) AS day_count
                    FROM gym_visit
                    WHERE member_id = :memberId AND status = 'ADMISSION'
                    GROUP BY EXTRACT(ISOYEAR FROM admitted_at), EXTRACT(WEEK FROM admitted_at)
                ) weekly
                """.trimIndent(),
            )
        query.setParameter("memberId", memberId)
        return (query.singleResult as Number).toInt()
    }

    /**
     * 05~09시 입장 이력 존재 여부
     */
    override fun existsEarlyVisit(memberId: Long): Boolean {
        val query =
            entityManager.createNativeQuery(
                """
                SELECT CASE WHEN EXISTS (
                    SELECT 1 FROM gym_visit
                    WHERE member_id = :memberId AND status = 'ADMISSION'
                    AND EXTRACT(HOUR FROM admitted_at) >= 5
                    AND EXTRACT(HOUR FROM admitted_at) < 9
                ) THEN 1 ELSE 0 END
                """.trimIndent(),
            )
        query.setParameter("memberId", memberId)
        return (query.singleResult as Number).toInt() == 1
    }

    /**
     * 22~02시 입장 이력 존재 여부
     */
    override fun existsNightVisit(memberId: Long): Boolean {
        val query =
            entityManager.createNativeQuery(
                """
                SELECT CASE WHEN EXISTS (
                    SELECT 1 FROM gym_visit
                    WHERE member_id = :memberId AND status = 'ADMISSION'
                    AND (EXTRACT(HOUR FROM admitted_at) >= 22
                         OR EXTRACT(HOUR FROM admitted_at) < 2)
                ) THEN 1 ELSE 0 END
                """.trimIndent(),
            )
        query.setParameter("memberId", memberId)
        return (query.singleResult as Number).toInt() == 1
    }

    /**
     * 오픈 30분 이내 입장 이력 존재 여부
     * 오픈 기준: 10:00 (gym에 openingHour 없으므로 고정값)
     */
    override fun existsOpenerVisit(memberId: Long): Boolean {
        val query =
            entityManager.createNativeQuery(
                """
                SELECT CASE WHEN EXISTS (
                    SELECT 1 FROM gym_visit
                    WHERE member_id = :memberId AND status = 'ADMISSION'
                    AND EXTRACT(HOUR FROM admitted_at) = 10
                    AND EXTRACT(MINUTE FROM admitted_at) < 30
                ) THEN 1 ELSE 0 END
                """.trimIndent(),
            )
        query.setParameter("memberId", memberId)
        return (query.singleResult as Number).toInt() == 1
    }

    /**
     * 48시간 내 2개 이상 암장 방문 이력 존재 여부
     * 각 방문에 대해 48시간 윈도우 내 다른 암장 방문이 있는지 확인
     */
    override fun existsWandererVisit(memberId: Long): Boolean {
        val query =
            entityManager.createNativeQuery(
                """
                SELECT CASE WHEN EXISTS (
                    SELECT 1 FROM gym_visit v1
                    JOIN gym_visit v2 ON v1.member_id = v2.member_id
                        AND v1.id <> v2.id
                        AND v1.gym_id <> v2.gym_id
                        AND v2.admitted_at BETWEEN v1.admitted_at AND v1.admitted_at + INTERVAL '48 hours'
                    WHERE v1.member_id = :memberId
                    AND v1.status = 'ADMISSION'
                    AND v2.status = 'ADMISSION'
                ) THEN 1 ELSE 0 END
                """.trimIndent(),
            )
        query.setParameter("memberId", memberId)
        return (query.singleResult as Number).toInt() == 1
    }
}

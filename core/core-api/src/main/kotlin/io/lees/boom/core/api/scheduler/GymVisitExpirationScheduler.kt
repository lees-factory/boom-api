package io.lees.boom.core.api.scheduler

import io.lees.boom.core.domain.GymService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * 오래된 암장 방문 기록을 정리하는 스케줄러
 *
 * 앱에서 대부분의 퇴장 처리를 담당하고,
 * 이 스케줄러는 앱이 죽거나 삭제된 경우 등 예외 상황만 처리합니다.
 */
@Component
class GymVisitExpirationScheduler(
    private val gymService: GymService,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 매일 새벽 4시에 만료된 방문 기록 일괄 정리
     * - 앱이 죽거나 삭제된 경우
     * - 폰이 꺼진 경우
     * - 네트워크 끊긴 경우 등 예외 상황 처리
     */
    @Scheduled(cron = "0 0 4 * * *") // 매일 새벽 4시
    fun cleanupExpiredVisits() {
        try {
            val cleanedCount = gymService.cleanupExpiredVisits()
            if (cleanedCount > 0) {
                log.info("만료된 방문 기록 정리 완료: {}건", cleanedCount)
            }
        } catch (e: Exception) {
            log.error("만료된 방문 기록 정리 중 오류 발생", e)
        }
    }
}

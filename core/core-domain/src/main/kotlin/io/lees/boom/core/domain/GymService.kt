package io.lees.boom.core.domain

import io.lees.boom.core.enums.VisitStatus
import io.lees.boom.core.error.CoreErrorType
import io.lees.boom.core.error.CoreException
import io.lees.boom.core.support.PageRequest
import io.lees.boom.core.support.SliceResult
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class GymService(
    private val gymReader: GymReader,
    private val gymUpdater: GymUpdater,
    private val gymActiveVisitReader: GymActiveVisitReader,
    private val gymActiveVisitWriter: GymActiveVisitWriter,
    private val gymVisitAppender: GymVisitAppender,
    private val crowdLevelCalculator: CrowdLevelCalculator,
    private val locationCalculator: LocationCalculator,
) {
    /**
     * 입장 처리
     * 1. 이미 입장 중이면 에러
     * 2. gym_active_visit에 INSERT (현재 상태)
     * 3. gym_visit에 INSERT (히스토리/통계용)
     * 4. 암장 인원 증가
     */
    @Transactional
    fun enterUser(
        gymId: Long,
        memberId: Long,
    ) {
        // 1. 이미 입장 중인 암장이 있는지 확인
        if (gymActiveVisitReader.existsActiveVisit(memberId)) {
            throw CoreException(CoreErrorType.ALREADY_ADMITTED)
        }

        // 2. 암장 정보 조회
        val gym = gymReader.read(gymId)

        // 3. 현재 입장 상태 저장 (gym_active_visit)
        val activeVisit = GymActiveVisit.create(gymId, memberId)
        gymActiveVisitWriter.save(activeVisit)

        // 4. 방문 히스토리 저장 (gym_visit) - 통계용
        val visitHistory = GymVisit.createAdmission(gymId, memberId)
        gymVisitAppender.append(visitHistory)

        // 5. 암장 인원 증가
        val newCount = gym.currentCount + 1
        val newLevel = crowdLevelCalculator.calculate(newCount, gym.maxCapacity)

        val updatedGym =
            gym.copy(
                currentCount = newCount,
                crowdLevel = newLevel,
            )
        gymUpdater.update(updatedGym)
    }

    /**
     * 퇴장 처리
     * 1. 입장 기록 확인
     * 2. gym_active_visit에서 DELETE
     * 3. gym_visit에 EXIT 기록 INSERT (히스토리/통계용)
     * 4. 암장 인원 감소
     */
    @Transactional
    fun exitUser(
        gymId: Long,
        memberId: Long,
    ) {
        // 1. 입장 기록 확인
        val activeVisit =
            gymActiveVisitReader.readActiveVisit(gymId, memberId)
                ?: throw CoreException(CoreErrorType.NOT_ADMITTED)

        // 2. 현재 입장 상태 삭제 (gym_active_visit)
        gymActiveVisitWriter.delete(memberId)

        // 3. 퇴장 히스토리 저장 (gym_visit) - 통계용
        val exitHistory =
            GymVisit(
                gymId = gymId,
                memberId = memberId,
                status = VisitStatus.EXIT,
                admittedAt = activeVisit.admittedAt,
                exitedAt = java.time.LocalDateTime.now(),
                expiresAt = activeVisit.expiresAt,
            )
        gymVisitAppender.append(exitHistory)

        // 4. 암장 인원 감소
        decreaseGymCount(gymId)
    }

    /**
     * 방문 연장 (기본 3시간 추가)
     */
    @Transactional
    fun extendVisit(
        gymId: Long,
        memberId: Long,
    ) {
        val activeVisit =
            gymActiveVisitReader.readActiveVisit(gymId, memberId)
                ?: throw CoreException(CoreErrorType.NOT_ADMITTED)

        val extendedVisit = activeVisit.extend()
        gymActiveVisitWriter.save(extendedVisit)
    }

    /**
     * 만료된 방문 기록 일괄 정리 (스케줄러에서 호출)
     * gym_active_visit에서 만료된 항목을 퇴장 처리합니다.
     *
     * @return 퇴장 처리된 방문 수
     */
    @Transactional
    fun cleanupExpiredVisits(): Int {
        val expiredVisits = gymActiveVisitReader.readExpiredVisits()
        if (expiredVisits.isEmpty()) return 0

        // gymId별로 그룹화하여 count 업데이트 최적화
        val visitsByGym = expiredVisits.groupBy { it.gymId }

        visitsByGym.forEach { (gymId, visits) ->
            visits.forEach { activeVisit ->
                // 현재 입장 상태 삭제
                gymActiveVisitWriter.delete(activeVisit.memberId)

                // 퇴장 히스토리 저장 (통계용)
                val exitHistory =
                    GymVisit(
                        gymId = activeVisit.gymId,
                        memberId = activeVisit.memberId,
                        status = VisitStatus.EXIT,
                        admittedAt = activeVisit.admittedAt,
                        exitedAt = java.time.LocalDateTime.now(),
                        expiresAt = activeVisit.expiresAt,
                    )
                gymVisitAppender.append(exitHistory)
            }

            // 암장 인원 일괄 감소
            decreaseGymCount(gymId, visits.size)
        }

        return expiredVisits.size
    }

    /**
     * 암장 인원 감소 (내부 메서드)
     */
    private fun decreaseGymCount(
        gymId: Long,
        count: Int = 1,
    ) {
        val gym = gymReader.read(gymId)
        val newCount = (gym.currentCount - count).coerceAtLeast(0)
        val newLevel = crowdLevelCalculator.calculate(newCount, gym.maxCapacity)

        val updatedGym =
            gym.copy(
                currentCount = newCount,
                crowdLevel = newLevel,
            )
        gymUpdater.update(updatedGym)
    }

    // --- 조회 메서드들 ---

    fun getGymsOnMap(
        southWestLatitude: Double,
        southWestLongitude: Double,
        northEastLatitude: Double,
        northEastLongitude: Double,
    ): List<Gym> =
        gymReader.readInViewport(
            southWestLatitude = southWestLatitude,
            southWestLongitude = southWestLongitude,
            northEastLatitude = northEastLatitude,
            northEastLongitude = northEastLongitude,
        )

    fun getGymsByRadius(
        latitude: Double,
        longitude: Double,
        radiusKm: Double,
    ): List<Gym> {
        val center = Location.create(latitude, longitude)
        val viewport = locationCalculator.calculateViewport(center, radiusKm)

        return gymReader.readInViewport(
            southWestLatitude = viewport.southWest.latitude,
            southWestLongitude = viewport.southWest.longitude,
            northEastLatitude = viewport.northEast.latitude,
            northEastLongitude = viewport.northEast.longitude,
        )
    }

    /**
     * 반경 내 암장 목록 조회 (무한스크롤용 Slice)
     */
    fun getGymsByRadiusSlice(
        latitude: Double,
        longitude: Double,
        radiusKm: Double,
        pageRequest: PageRequest,
    ): SliceResult<Gym> {
        val center = Location.create(latitude, longitude)
        val viewport = locationCalculator.calculateViewport(center, radiusKm)

        return gymReader.readInViewportSlice(
            southWestLatitude = viewport.southWest.latitude,
            southWestLongitude = viewport.southWest.longitude,
            northEastLatitude = viewport.northEast.latitude,
            northEastLongitude = viewport.northEast.longitude,
            pageRequest = pageRequest,
        )
    }

    /**
     * 특정 암장의 현재 입장 유저 목록 조회
     */
    fun getGymVisitors(
        gymId: Long,
        pageRequest: PageRequest,
    ): SliceResult<GymVisitor> = gymActiveVisitReader.readActiveVisitors(gymId, pageRequest)

    /**
     * 현재 입장중인 암장 조회
     * 입장중인 암장이 없으면 null 반환
     */
    fun getCurrentVisit(memberId: Long): CurrentVisit? {
        val activeVisit = gymActiveVisitReader.readActiveVisit(memberId) ?: return null
        val gym = gymReader.read(activeVisit.gymId)
        return CurrentVisit(gym = gym, activeVisit = activeVisit)
    }
}

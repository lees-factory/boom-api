package io.lees.boom.core.domain

import io.lees.boom.core.error.CoreErrorType
import io.lees.boom.core.error.CoreException
import org.springframework.stereotype.Service

@Service
class GymService(
    private val gymReader: GymReader,
    private val gymUpdater: GymUpdater,
    private val gymVisitReader: GymVisitReader, // [추가] 방문 기록 조회
    private val gymVisitAppender: GymVisitAppender, // [추가] 방문 기록 저장
    private val crowdLevelCalculator: CrowdLevelCalculator,
    private val locationCalculator: LocationCalculator,
) {
    /**
     * 입장 처리 (중복 방지 로직 적용)
     */
    fun enterUser(
        gymId: Long,
        memberId: Long,
    ) {
        // 1. 이미 입장 중인 암장이 있는지 확인 (격벽: 유효성 검증)
        if (gymVisitReader.existsActiveVisit(memberId)) {
            // 이미 입장 중이라면 무시하거나 에러 처리
            // 여기서는 멱등성(여러번 눌러도 결과 동일)을 위해 에러 대신 단순 리턴(무시) 처리하거나,
            // 클라이언트에게 알리기 위해 에러를 던질 수 있습니다.
            // "이미 입장 처리되었습니다" 라고 응답하기 위해 에러를 던집니다.
            throw CoreException(CoreErrorType.ALREADY_ADMITTED)
        }

        // 2. 암장 정보 조회
        val gym = gymReader.read(gymId)

        // 3. 방문 기록 생성 (Visit 개념)
        val visit = GymVisit.createAdmission(gymId, memberId)
        gymVisitAppender.append(visit)

        // 4. 암장 인원 증가 (Gym 개념)
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
     */
    fun exitUser(
        gymId: Long,
        memberId: Long,
    ) {
        // 1. 입장 기록 확인
        val activeVisit =
            gymVisitReader.readActiveVisit(gymId, memberId)
                ?: throw CoreException(CoreErrorType.NOT_ADMITTED)

        // 2. 방문 기록 '퇴장' 처리
        val exitedVisit = activeVisit.exit()
        gymVisitAppender.append(exitedVisit)

        // 3. 암장 인원 감소
        val gym = gymReader.read(gymId)
        val newCount = (gym.currentCount - 1).coerceAtLeast(0)
        val newLevel = crowdLevelCalculator.calculate(newCount, gym.maxCapacity)

        val updatedGym =
            gym.copy(
                currentCount = newCount,
                crowdLevel = newLevel,
            )
        gymUpdater.update(updatedGym)
    }

    // ... 조회 메서드들 유지 ...
    fun getGymsOnMap(
        southWestLatitude: Double,
        southWestLongitude: Double,
        northEastLatitude: Double,
        northEastLongitude: Double,
    ): List<Gym> =
        gymReader.readInViewport(
            southWestLatitude,
            northEastLatitude,
            southWestLongitude,
            northEastLongitude,
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
            northEastLatitude = viewport.northEast.latitude,
            southWestLongitude = viewport.southWest.longitude,
            northEastLongitude = viewport.northEast.longitude,
        )
    }
}

package io.lees.boom.core.domain

import io.lees.boom.core.enums.CrewRole
import io.lees.boom.core.error.CoreErrorType
import io.lees.boom.core.error.CoreException
import org.springframework.stereotype.Service
import java.io.InputStream
import java.time.LocalDateTime
import java.util.UUID

@Service
class CrewService(
    private val crewAppender: CrewAppender,
    private val crewReader: CrewReader,
    private val crewMemberReader: CrewMemberReader,
    private val crewScheduleAppender: CrewScheduleAppender,
    private val crewScheduleReader: CrewScheduleReader,
    private val activityScoreUpdater: ActivityScoreUpdater,
    private val imageStorage: ImageStorage,
) {
    /**
     * 크루 만들기
     * 1. 크루 정보 저장
     * 2. 생성자를 리더로 하여 크루원 정보 저장
     */
    fun createCrew(
        memberId: Long,
        name: String,
        description: String,
        crewImageInput: CrewImageInput?,
        maxMemberCount: Int,
        latitude: Double?,
        longitude: Double?,
        address: String?,
    ): Crew {
        val crewImageUrl =
            crewImageInput?.let {
                val path = "crew/${UUID.randomUUID()}"
                imageStorage.upload(path, it.inputStream, it.contentType, it.contentLength)
            }

        val newCrew = Crew.create(name, description, crewImageUrl, maxMemberCount, latitude, longitude, address)
        return crewAppender.appendCrewWithLeader(newCrew, memberId)
    }

    /**
     * 크루 상세 조회
     */
    fun getCrew(crewId: Long): Crew =
        crewReader.readById(crewId)
            ?: throw CoreException(CoreErrorType.CREW_NOT_FOUND)

    /**
     * 크루 가입하기 (초대/직접 가입 등 흐름에 따라 메서드 분화 가능)
     */
    fun joinCrew(
        memberId: Long,
        crewId: Long,
    ) {
        // 이미 가입된 유저인지 검증
        crewMemberReader.readCrewMember(crewId, memberId)?.let {
            throw CoreException(CoreErrorType.CREW_ALREADY_JOINED)
        }

        // 크루 정원 초과 여부 확인
        val crew =
            crewReader.readById(crewId)
                ?: throw CoreException(CoreErrorType.CREW_NOT_FOUND)

        if (crew.memberCount >= crew.maxMemberCount) {
            throw CoreException(CoreErrorType.CREW_MEMBER_LIMIT_EXCEEDED)
        }

        val newMember = CrewMember.createMember(crewId, memberId)
        crewAppender.appendMemberWithCount(newMember)
    }

    /**
     * 내 크루 목록 조회
     */
    fun getMyCrews(memberId: Long): List<MyCrewInfo> = crewReader.readMyCrews(memberId)

    /**
     * 크루 멤버 목록 조회
     */
    fun getCrewMembers(crewId: Long): List<CrewMemberInfo> = crewMemberReader.readCrewMembers(crewId)

    /**
     * 크루 일정 등록
     * LEADER, MEMBER만 접근 가능 (GUEST 제외)
     */
    fun createSchedule(
        crewId: Long,
        memberId: Long,
        gymId: Long?,
        title: String,
        description: String,
        scheduledAt: LocalDateTime,
    ): CrewSchedule {
        val crewMember =
            crewMemberReader.readCrewMember(crewId, memberId)
                ?: throw CoreException(CoreErrorType.CREW_MEMBER_NOT_AUTHORIZED)

        if (crewMember.role == CrewRole.GUEST) {
            throw CoreException(CoreErrorType.CREW_MEMBER_NOT_AUTHORIZED)
        }

        val schedule =
            CrewSchedule.create(
                crewId = crewId,
                gymId = gymId,
                title = title,
                description = description,
                scheduledAt = scheduledAt,
                createdBy = memberId,
            )
        return crewScheduleAppender.append(schedule)
    }

    /**
     * 크루 일정 목록 조회
     * 크루 멤버만 접근 가능
     */
    fun getSchedules(
        crewId: Long,
        memberId: Long,
    ): List<CrewSchedule> {
        crewMemberReader.readCrewMember(crewId, memberId)
            ?: throw CoreException(CoreErrorType.CREW_MEMBER_NOT_AUTHORIZED)

        return crewScheduleReader.readByCrewId(crewId)
    }

    /**
     * 크루 일정 참여
     * LEADER, MEMBER만 접근 가능 (GUEST 제외)
     */
    fun participateSchedule(
        crewId: Long,
        scheduleId: Long,
        memberId: Long,
    ) {
        // 크루 멤버 권한 확인
        val crewMember =
            crewMemberReader.readCrewMember(crewId, memberId)
                ?: throw CoreException(CoreErrorType.CREW_MEMBER_NOT_AUTHORIZED)

        if (crewMember.role == CrewRole.GUEST) {
            throw CoreException(CoreErrorType.CREW_MEMBER_NOT_AUTHORIZED)
        }

        // 일정 존재 확인
        val schedule =
            crewScheduleReader.readById(scheduleId)
                ?: throw CoreException(CoreErrorType.SCHEDULE_NOT_FOUND)

        if (schedule.crewId != crewId) {
            throw CoreException(CoreErrorType.SCHEDULE_NOT_FOUND)
        }

        // 중복 참여 확인
        crewScheduleReader.readParticipant(scheduleId, memberId)?.let {
            throw CoreException(CoreErrorType.SCHEDULE_ALREADY_PARTICIPATED)
        }

        val participant = CrewScheduleParticipant.create(scheduleId, memberId)
        crewScheduleAppender.appendParticipant(participant)

        // 활동 점수 +10
        activityScoreUpdater.addScheduleParticipateScore(memberId)
    }

    /**
     * 크루 일정 참여자 목록 조회
     * 크루 멤버만 접근 가능
     */
    fun getScheduleParticipants(
        crewId: Long,
        scheduleId: Long,
        memberId: Long,
    ): List<CrewScheduleParticipantInfo> {
        crewMemberReader.readCrewMember(crewId, memberId)
            ?: throw CoreException(CoreErrorType.CREW_MEMBER_NOT_AUTHORIZED)

        return crewScheduleReader.readParticipants(scheduleId)
    }

    /**
     * 내 주변 크루 찾기 (동네 크루)
     */
    fun getLocalCrews(
        latitude: Double,
        longitude: Double,
        page: Int,
        size: Int,
    ): List<Crew> = crewReader.readLocalCrews(latitude, longitude, page, size)

    /**
     * 크루 랭킹 조회
     * 크루원들의 평균 활동 점수 기반
     */
    fun getCrewRanking(
        page: Int,
        size: Int,
    ): List<CrewRankingInfo> = crewReader.readCrewRankingByAvgScore(page, size)
}

data class CrewImageInput(
    val inputStream: InputStream,
    val contentType: String,
    val contentLength: Long,
)

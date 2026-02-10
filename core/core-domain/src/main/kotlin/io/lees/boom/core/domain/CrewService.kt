package io.lees.boom.core.domain

import io.lees.boom.core.enums.CrewRole
import io.lees.boom.core.error.CoreErrorType
import io.lees.boom.core.error.CoreException
import jakarta.transaction.Transactional
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
     * 크루 수정 (LEADER 전용)
     * 인원수, 이미지만 변경 가능
     */
    @Transactional
    fun updateCrew(
        crewId: Long,
        memberId: Long,
        crewImageInput: CrewImageInput?,
        maxMemberCount: Int?,
    ) {
        val crewMember =
            crewMemberReader.readCrewMember(crewId, memberId)
                ?: throw CoreException(CoreErrorType.CREW_MEMBER_NOT_AUTHORIZED)

        if (crewMember.role != CrewRole.LEADER) {
            throw CoreException(CoreErrorType.CREW_MEMBER_NOT_AUTHORIZED)
        }

        // maxMemberCount 검증: 현재 인원보다 적게 설정 불가
        if (maxMemberCount != null) {
            val crew =
                crewReader.readById(crewId)
                    ?: throw CoreException(CoreErrorType.CREW_NOT_FOUND)
            if (maxMemberCount < crew.memberCount) {
                throw CoreException(CoreErrorType.CREW_MAX_MEMBER_COUNT_TOO_SMALL)
            }
        }

        val crewImageUrl =
            crewImageInput?.let {
                val path = "crew/${UUID.randomUUID()}"
                imageStorage.upload(path, it.inputStream, it.contentType, it.contentLength)
            }

        crewAppender.updateCrew(crewId, maxMemberCount, crewImageUrl)
    }

    /**
     * 크루 상세 조회
     */
    fun getCrew(crewId: Long): Crew =
        crewReader.readById(crewId)
            ?: throw CoreException(CoreErrorType.CREW_NOT_FOUND)

    /**
     * 크루 가입하기
     * 비관적 락으로 크루 삭제와의 동시성 보장
     */
    @Transactional
    fun joinCrew(
        memberId: Long,
        crewId: Long,
    ) {
        // 이미 가입된 유저인지 검증
        crewMemberReader.readCrewMember(crewId, memberId)?.let {
            throw CoreException(CoreErrorType.CREW_ALREADY_JOINED)
        }

        // 비관적 락으로 크루 조회 (삭제와 동시성 보장)
        val crew =
            crewReader.readByIdForUpdate(crewId)
                ?: throw CoreException(CoreErrorType.CREW_NOT_FOUND)

        if (crew.memberCount >= crew.maxMemberCount) {
            throw CoreException(CoreErrorType.CREW_MEMBER_LIMIT_EXCEEDED)
        }

        val newMember = CrewMember.createMember(crewId, memberId)
        crewAppender.appendMember(newMember)
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
        val savedSchedule = crewScheduleAppender.append(schedule)

        // 생성자를 자동으로 참여자에 추가
        val participant = CrewScheduleParticipant.create(savedSchedule.id!!, memberId)
        crewScheduleAppender.appendParticipant(participant)
        activityScoreUpdater.addScheduleParticipateScore(memberId)

        return savedSchedule
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

        val schedule =
            crewScheduleReader.readById(scheduleId)
                ?: throw CoreException(CoreErrorType.SCHEDULE_NOT_FOUND)

        return crewScheduleReader.readParticipants(scheduleId).map {
            it.copy(isCreator = it.memberId == schedule.createdBy)
        }
    }

    /**
     * 크루 탈퇴 (MEMBER/GUEST만 가능)
     * LEADER는 크루 삭제(deleteCrew)를 이용해야 함
     */
    @Transactional
    fun leaveCrew(
        crewId: Long,
        memberId: Long,
    ) {
        val crewMember =
            crewMemberReader.readCrewMember(crewId, memberId)
                ?: throw CoreException(CoreErrorType.CREW_NOT_MEMBER)

        if (crewMember.role == CrewRole.LEADER) {
            throw CoreException(CoreErrorType.CREW_LEADER_CANNOT_LEAVE)
        }

        crewAppender.softDeleteMember(crewMember.id!!)
    }

    /**
     * 크루 삭제 (LEADER 전용, 혼자일 때만)
     * 비관적 락으로 크루 가입과의 동시성 보장
     */
    @Transactional
    fun deleteCrew(
        crewId: Long,
        memberId: Long,
    ) {
        val crewMember =
            crewMemberReader.readCrewMember(crewId, memberId)
                ?: throw CoreException(CoreErrorType.CREW_NOT_MEMBER)

        if (crewMember.role != CrewRole.LEADER) {
            throw CoreException(CoreErrorType.CREW_MEMBER_NOT_AUTHORIZED)
        }

        // 비관적 락으로 크루 조회 (가입과 동시성 보장)
        val crew =
            crewReader.readByIdForUpdate(crewId)
                ?: throw CoreException(CoreErrorType.CREW_NOT_FOUND)

        // 리더 혼자일 때만 삭제 가능 (서버 사이드 검증)
        if (crew.memberCount > 1) {
            throw CoreException(CoreErrorType.CREW_DELETE_NOT_ALLOWED)
        }

        crewAppender.softDeleteAllMembersByCrewId(crewId)
        crewAppender.softDeleteCrew(crewId)
    }

    /**
     * 크루 일정 참여 취소
     * 참여 기록 hard delete + 활동점수 -10
     */
    fun cancelScheduleParticipation(
        crewId: Long,
        scheduleId: Long,
        memberId: Long,
    ) {
        // 크루 멤버 확인
        crewMemberReader.readCrewMember(crewId, memberId)
            ?: throw CoreException(CoreErrorType.CREW_NOT_MEMBER)

        // 참여 기록 확인
        crewScheduleReader.readParticipant(scheduleId, memberId)
            ?: throw CoreException(CoreErrorType.SCHEDULE_NOT_PARTICIPATED)

        // 참여 기록 삭제
        crewScheduleAppender.removeParticipant(scheduleId, memberId)

        // 활동점수 -10
        activityScoreUpdater.subtractScheduleParticipateScore(memberId)
    }

    /**
     * 크루 일정 삭제
     * - LEADER 또는 일정 생성자만 삭제 가능
     * - scheduledAt이 현재 시간 이후인 경우에만 삭제 가능
     * - 참여자 전원 활동점수 -10 + 참여자/일정 hard delete
     */
    fun deleteSchedule(
        crewId: Long,
        scheduleId: Long,
        memberId: Long,
    ) {
        // 크루 멤버 확인
        val crewMember =
            crewMemberReader.readCrewMember(crewId, memberId)
                ?: throw CoreException(CoreErrorType.CREW_MEMBER_NOT_AUTHORIZED)

        // 일정 존재 + 크루 소속 확인
        val schedule =
            crewScheduleReader.readById(scheduleId)
                ?: throw CoreException(CoreErrorType.SCHEDULE_NOT_FOUND)

        if (schedule.crewId != crewId) {
            throw CoreException(CoreErrorType.SCHEDULE_NOT_FOUND)
        }

        // LEADER 또는 일정 생성자만 삭제 가능
        if (crewMember.role != CrewRole.LEADER && schedule.createdBy != memberId) {
            throw CoreException(CoreErrorType.CREW_MEMBER_NOT_AUTHORIZED)
        }

        // 이미 지난 일정은 삭제 불가
        if (schedule.scheduledAt.isBefore(LocalDateTime.now())) {
            throw CoreException(CoreErrorType.SCHEDULE_ALREADY_PASSED)
        }

        // 참여자 활동점수 차감
        val participantMemberIds = crewScheduleReader.readParticipantMemberIds(scheduleId)
        participantMemberIds.forEach { participantMemberId ->
            activityScoreUpdater.subtractScheduleParticipateScore(participantMemberId)
        }

        // 참여자 전원 삭제 → 일정 삭제
        crewScheduleAppender.removeParticipantsByScheduleId(scheduleId)
        crewScheduleAppender.removeSchedule(scheduleId)
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

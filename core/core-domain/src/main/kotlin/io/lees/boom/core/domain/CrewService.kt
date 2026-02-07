package io.lees.boom.core.domain

import io.lees.boom.core.enums.CrewRole
import io.lees.boom.core.error.CoreErrorType
import io.lees.boom.core.error.CoreException
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class CrewService(
    private val crewAppender: CrewAppender,
    private val crewReader: CrewReader,
    private val crewMemberReader: CrewMemberReader,
    private val crewScheduleAppender: CrewScheduleAppender,
    private val crewScheduleReader: CrewScheduleReader,
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
        maxMemberCount: Int,
    ): Crew {
        // 1. 크루 생성
        val newCrew = Crew.create(name, description, maxMemberCount)
        val savedCrew = crewAppender.append(newCrew)
        if (savedCrew.id == null) {
            throw CoreException(CoreErrorType.CREW_CREATE_ERROR)
        }

        // 2. 생성자를 리더로 등록
        val leader =
            CrewMember.createLeader(
                crewId = savedCrew.id,
                memberId = memberId,
            )
        crewAppender.appendMember(leader)

        return savedCrew
    }

    /**
     * 크루 가입하기 (초대/직접 가입 등 흐름에 따라 메서드 분화 가능)
     */
    fun joinCrew(
        memberId: Long,
        crewId: Long,
    ) {
        // TODO: 이미 가입된 유저인지 검증 (Validator or Reader)
        // TODO: 크루 정원 초과 여부 확인

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
}

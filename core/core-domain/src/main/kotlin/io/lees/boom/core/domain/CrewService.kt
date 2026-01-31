package io.lees.boom.core.domain

import io.lees.boom.core.error.CoreErrorType
import io.lees.boom.core.error.CoreException
import org.springframework.stereotype.Service

@Service
class CrewService(
    private val crewAppender: CrewAppender,
    // 추후 CrewFinder, CrewValidator 등이 추가될 수 있음
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
                crewId = savedCrew.id, // Smart Cast or Safe Access (위에서 검증됨)
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
}

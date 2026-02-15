package io.lees.boom.core.domain.crew

import io.lees.boom.core.enums.CrewRole

data class CrewMember(
    val id: Long? = null,
    val crewId: Long, // 어떤 크루인가
    val memberId: Long, // 누구인가
    val role: CrewRole, // 그 크루에서 어떤 권한인가
) {
    companion object {
        // 크루를 생성할 때, 생성자는 자동으로 LEADER가 됨
        fun createLeader(
            crewId: Long,
            memberId: Long,
        ): CrewMember =
            CrewMember(
                crewId = crewId,
                memberId = memberId,
                role = CrewRole.LEADER,
            )

        // 일반 멤버로 가입/초대 될 때
        fun createMember(
            crewId: Long,
            memberId: Long,
        ): CrewMember =
            CrewMember(
                crewId = crewId,
                memberId = memberId,
                role = CrewRole.MEMBER,
            )
    }
}

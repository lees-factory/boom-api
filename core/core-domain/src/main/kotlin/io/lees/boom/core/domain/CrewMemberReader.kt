package io.lees.boom.core.domain

import org.springframework.stereotype.Component

@Component
class CrewMemberReader(
    private val crewRepository: CrewRepository,
) {
    fun readMyCrewMemberIds(memberId: Long): Set<Long> {
        val crewIds = crewRepository.findCrewIdsByMemberId(memberId)
        if (crewIds.isEmpty()) return emptySet()
        return crewRepository.findMemberIdsByCrewIds(crewIds) - memberId
    }

    fun readCrewMembers(crewId: Long): List<CrewMemberInfo> = crewRepository.findMembersWithInfoByCrewId(crewId)

    fun readCrewMember(
        crewId: Long,
        memberId: Long,
    ): CrewMember? = crewRepository.findMemberByCrewIdAndMemberId(crewId, memberId)
}

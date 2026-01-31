package io.lees.boom.core.api.controller.v1.request

import io.lees.boom.core.domain.Crew

data class CrewResponse(
    val id: Long,
)

data class CrewResponse(
    val id: Long,
    val name: String,
    val description: String,
    val maxMemberCount: Int,
) {
    companion object {
        fun from(crew: Crew): CrewResponse {
            return CrewResponse(
                id = crew.id ?: 0L,
                name = crew.name,
                description = crew.description,
                maxMemberCount = crew.maxMemberCount
            )
        }
    }
}
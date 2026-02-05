package io.lees.boom.core.domain

/**
 * 현재 입장중인 방문 정보
 */
data class CurrentVisit(
    val gym: Gym,
    val activeVisit: GymActiveVisit,
)

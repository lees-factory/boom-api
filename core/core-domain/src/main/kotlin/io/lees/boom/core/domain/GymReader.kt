package io.lees.boom.core.domain

import org.springframework.stereotype.Component

@Component
class GymReader(
    private val gymRepository:GymRepository
) {

    fun readInViewport(
        startLatitude: Double, endLatitude: Double,
        startLongitude: Double, endLongitude: Double
    ): List<Gym> {
        // 여기서 DB 구현체(GymCoreRepository)를 호출
        return gymRepository.findInBox(startLatitude, endLatitude, startLongitude, endLongitude)
    }
}

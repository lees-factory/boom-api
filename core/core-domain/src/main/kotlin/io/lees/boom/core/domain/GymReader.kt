package io.lees.boom.core.domain

import org.springframework.stereotype.Component

@Component
class GymReader(
    private val gymRepository: GymRepository,
) {
    fun readInViewport(
        southWestLatitude: Double,
        southWestLongitude: Double,
        northEastLatitude: Double,
        northEastLongitude: Double,
    ): List<Gym> {
        val southWest = Location.create(southWestLatitude, southWestLongitude)
        val northEast = Location.create(northEastLatitude, northEastLongitude)

        return gymRepository.findGymsWithinViewport(southWest, northEast)
    }
}

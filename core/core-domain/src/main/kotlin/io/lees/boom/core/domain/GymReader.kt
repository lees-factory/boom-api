package io.lees.boom.core.domain

import io.lees.boom.core.error.CoreErrorType
import io.lees.boom.core.error.CoreException
import org.springframework.stereotype.Component

@Component
class GymReader(
    private val gymRepository: GymRepository,
) {
    fun read(gymId: Long): Gym =
        gymRepository.findById(gymId)
            ?: throw CoreException(CoreErrorType.NOT_FOUND_DATA)

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

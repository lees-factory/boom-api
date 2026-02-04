package io.lees.boom.core.domain

interface GymRepository {
    fun save(gym: Gym): Gym

    fun findById(id: Long): Gym?

    fun findGymsWithinViewport(
        southWestLocation: Location,
        northEastLocation: Location,
    ): List<Gym>
}

package io.lees.boom.storage.db.core

import io.lees.boom.core.domain.Gym
import io.lees.boom.core.domain.GymRepository
import io.lees.boom.core.domain.Location
import io.lees.boom.core.error.CoreErrorType
import io.lees.boom.core.error.CoreException
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
internal class GymCoreRepository(
    private val gymJpaRepository: GymJpaRepository,
) : GymRepository {
    @Transactional
    override fun save(gym: Gym): Gym {
        val id = gym.id
        return if (gym.id != null) {
            // [UPDATE] ID가 있으면 조회 후 변경 (Dirty Checking)
            val entity =
                gymJpaRepository.findByIdOrNull(id as Long)
                    ?: throw CoreException(CoreErrorType.NOT_FOUND_DATA)

            entity.update(
                name = gym.name,
                address = gym.address,
                latitude = gym.location.latitude,
                longitude = gym.location.longitude,
                crowdLevel = gym.crowdLevel,
            )

            entity.toDomain()
        } else {
            gymJpaRepository.save(gym.toEntity()).toDomain()
        }
    }

    override fun findGymsWithinViewport(
        southWestLocation: Location,
        northEastLocation: Location,
    ): List<Gym> =
        gymJpaRepository
            .findByLatitudeBetweenAndLongitudeBetween(
                minimumLatitude = minOf(southWestLocation.latitude, northEastLocation.latitude),
                maximumLatitude = maxOf(southWestLocation.latitude, northEastLocation.latitude),
                minimumLongitude = minOf(southWestLocation.longitude, northEastLocation.longitude),
                maximumLongitude = maxOf(southWestLocation.longitude, northEastLocation.longitude),
            ).map { it.toDomain() }

    private fun Gym.toEntity() =
        GymEntity(
            name = this.name,
            address = this.address,
            latitude = this.location.latitude,
            longitude = this.location.longitude,
            crowdLevel = this.crowdLevel,
        )

    private fun GymEntity.toDomain() =
        Gym(
            id = this.id,
            name = this.name,
            address = this.address,
            location = Location.create(this.latitude, this.longitude),
            crowdLevel = this.crowdLevel,
        )
}

package io.lees.boom.storage.db.core

import io.lees.boom.core.domain.Gym
import io.lees.boom.core.domain.GymRepository
import io.lees.boom.core.domain.Location
import io.lees.boom.core.error.CoreErrorType
import io.lees.boom.core.error.CoreException
import io.lees.boom.core.support.PageRequest
import io.lees.boom.core.support.SliceResult
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import org.springframework.data.domain.PageRequest as JpaPageRequest

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

    override fun findById(id: Long): Gym? = gymJpaRepository.findByIdOrNull(id)?.toDomain()

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

    override fun findGymsWithinViewportSlice(
        southWestLocation: Location,
        northEastLocation: Location,
        pageRequest: PageRequest,
    ): SliceResult<Gym> {
        val minLat = minOf(southWestLocation.latitude, northEastLocation.latitude)
        val maxLat = maxOf(southWestLocation.latitude, northEastLocation.latitude)
        val minLon = minOf(southWestLocation.longitude, northEastLocation.longitude)
        val maxLon = maxOf(southWestLocation.longitude, northEastLocation.longitude)

        // limit+1 조회로 hasNext 판단
        val pageable = JpaPageRequest.of(pageRequest.page, pageRequest.size + 1)
        val entities =
            gymJpaRepository.findByViewportWithLimit(
                minLat = minLat,
                maxLat = maxLat,
                minLon = minLon,
                maxLon = maxLon,
                pageable = pageable,
            )

        val gyms = entities.map { it.toDomain() }
        return SliceResult.fromLimitPlusOne(gyms, pageRequest)
    }

    private fun Gym.toEntity() =
        GymEntity(
            name = this.name,
            address = this.address,
            latitude = this.location.latitude,
            longitude = this.location.longitude,
            maxCapacity = this.maxCapacity,
            currentCount = this.currentCount,
            crowdLevel = this.crowdLevel,
        )

    private fun GymEntity.toDomain() =
        Gym(
            id = this.id,
            name = this.name,
            address = this.address,
            location = Location.create(this.latitude, this.longitude),
            maxCapacity = this.maxCapacity,
            currentCount = this.currentCount,
            crowdLevel = this.crowdLevel,
        )
}

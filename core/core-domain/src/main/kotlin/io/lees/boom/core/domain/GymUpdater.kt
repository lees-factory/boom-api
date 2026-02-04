package io.lees.boom.core.domain

import org.springframework.stereotype.Component

@Component
class GymUpdater(
    private val gymRepository: GymRepository,
) {
    fun update(gym: Gym): Gym = gymRepository.save(gym)
}

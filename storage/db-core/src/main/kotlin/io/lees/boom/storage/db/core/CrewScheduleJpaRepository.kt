package io.lees.boom.storage.db.core

import org.springframework.data.jpa.repository.JpaRepository

interface CrewScheduleJpaRepository : JpaRepository<CrewScheduleEntity, Long> {
    fun findByCrewIdOrderByScheduledAtAsc(crewId: Long): List<CrewScheduleEntity>
}

package io.lees.boom.storage.db.core

import org.springframework.data.jpa.repository.JpaRepository

interface CrewJpaRepository : JpaRepository<CrewEntity, Long>

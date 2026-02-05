package io.lees.boom.storage.db.core

import org.springframework.data.jpa.repository.JpaRepository

/**
 * 방문 히스토리 JPA Repository (통계용)
 */
interface GymVisitJpaRepository : JpaRepository<GymVisitEntity, Long>

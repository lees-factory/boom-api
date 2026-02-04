package io.lees.boom.core.domain

import io.lees.boom.core.enums.CrowdLevel
import org.springframework.stereotype.Component

@Component
class CrowdLevelCalculator {
    fun calculate(
        currentCount: Int,
        maxCapacity: Int,
    ): CrowdLevel {
        if (maxCapacity == 0) return CrowdLevel.CROWDED

        val ratio = (currentCount.toDouble() / maxCapacity) * 100

        return when {
            ratio >= 80 -> CrowdLevel.CROWDED
            ratio >= 50 -> CrowdLevel.NORMAL
            else -> CrowdLevel.RELAXED
        }
    }
}

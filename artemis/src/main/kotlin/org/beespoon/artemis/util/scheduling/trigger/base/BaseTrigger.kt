package org.beespoon.artemis.util.scheduling.trigger.base

import java.time.Instant
import java.time.ZoneOffset
import kotlin.math.min

abstract class BaseTrigger {

    abstract fun getPeriodSecs(): Long

    abstract fun getStartEpochSecs(): Long

    fun getDelay(): Long {
        val delay = getStartEpochSecs() - Instant.now().atZone(ZoneOffset.UTC).toEpochSecond()
        return min(0, delay)
    }

}
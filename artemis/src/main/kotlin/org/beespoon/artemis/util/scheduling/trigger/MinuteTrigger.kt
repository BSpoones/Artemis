package org.beespoon.artemis.util.scheduling.trigger

import org.beespoon.artemis.util.scheduling.trigger.base.BaseTrigger
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

class MinuteTrigger(private val second: Int): BaseTrigger() {

    override fun getPeriodSecs(): Long = second.toLong()

    override fun getStartEpochSecs(): Long {
        return Instant.now().atOffset(ZoneOffset.UTC).toLocalDateTime()
            .withSecond(second)
            .let { if (it <= LocalDateTime.now(ZoneOffset.UTC)) it.plusMinutes(1) else it }
            .toInstant(ZoneOffset.UTC)
            .epochSecond
    }
}
package org.beespoon.artemis.util.scheduling.trigger

import org.beespoon.artemis.util.scheduling.trigger.base.BaseTrigger
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

class HourTrigger(private val minute: Int, private val second: Int): BaseTrigger() {

    constructor(
        minute: Int
    ): this(minute, 0)

    override fun getPeriodSecs(): Long = listOf(
        minute*60,
        second
    ).sum().toLong()

    override fun getStartEpochSecs(): Long {
        return Instant.now().atOffset(ZoneOffset.UTC).toLocalDateTime()
            .withSecond(second)
            .withMinute(minute)
            .let { if (it <= LocalDateTime.now(ZoneOffset.UTC)) it.plusHours(1) else it }
            .toInstant(ZoneOffset.UTC)
            .epochSecond
    }
}
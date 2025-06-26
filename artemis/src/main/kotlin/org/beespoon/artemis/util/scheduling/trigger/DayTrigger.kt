package org.beespoon.artemis.util.scheduling.trigger

import org.beespoon.artemis.util.scheduling.trigger.base.BaseTrigger
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

class DayTrigger(private val hour: Int, private val minute: Int, private val second: Int) : BaseTrigger() {

    constructor(
        hour: Int
    ) : this(hour, 0, 0)

    constructor(
        hour: Int,
        minute: Int,
    ) : this(hour, minute, 0)

    override fun getPeriodSecs(): Long = listOf(
        hour * 60 * 60,
        minute * 60,
        second
    ).sum().toLong()

    override fun getStartEpochSecs(): Long {
        return Instant.now().atOffset(ZoneOffset.UTC).toLocalDateTime()
            .withSecond(second)
            .withMinute(minute)
            .withHour(hour)
            .let { if (it <= LocalDateTime.now(ZoneOffset.UTC)) it.plusDays(1) else it }
            .toInstant(ZoneOffset.UTC)
            .epochSecond
    }
}
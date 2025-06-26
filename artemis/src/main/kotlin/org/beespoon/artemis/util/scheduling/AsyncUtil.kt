package org.beespoon.artemis.util.scheduling

import java.util.concurrent.Executors
import com.google.common.util.concurrent.ThreadFactoryBuilder
import org.beespoon.artemis.util.scheduling.trigger.base.BaseTrigger
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

fun async(unit: () -> Unit) = AsyncUtil.runAsync(unit)
fun <T> supplyAsync(task: () -> CompletableFuture<T>) = AsyncUtil.supplyAsync(task)

fun delayTask(delay: Duration, task: () -> Unit) = AsyncUtil.delayedTask(delay, task)
object AsyncUtil {
    private val EXECUTOR = Executors.newScheduledThreadPool(
        2,
        ThreadFactoryBuilder()
            .setDaemon(true)
            .build()
    )

    fun runAsync(task: () -> Unit) {
        EXECUTOR.execute {
            task.invoke()
        }
    }

    fun schedule(trigger: BaseTrigger, task: () -> Unit): ScheduledFuture<*> {
        return EXECUTOR.scheduleAtFixedRate(
            task,
            trigger.getDelay(),
            trigger.getPeriodSecs(),
            TimeUnit.SECONDS
        )
    }

    fun delayedTask(delay: Duration, task: () -> Unit): ScheduledFuture<*> {
        return EXECUTOR.schedule(
            task,
            delay.seconds,
            TimeUnit.SECONDS
        )
    }

    fun <T> supplyAsync(task: () -> T): CompletableFuture<T> {
        return CompletableFuture.supplyAsync(task, EXECUTOR)
    }
}
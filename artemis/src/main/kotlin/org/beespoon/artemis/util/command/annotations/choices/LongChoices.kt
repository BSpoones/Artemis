package org.beespoon.artemis.util.command.annotations.choices

/**
 * Register an array of [Long] choices.
 *
 * This annotation will automatically register the command choices set here.
 *
 * Example usage:
 * ```kotlin
 * fun myCommand(
 *     event: // Command event
 *
 *     @CommandOption
 *     @DoubleChoices([1L, 2L, 3L])
 *     channel: Channel
 * ) {}
 * ```
 * @property choices Array of [Long].
 * @author <a href="https://www.bspoones.com">BSpoones</a>
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class LongChoices(
    val choices: LongArray = [],
)
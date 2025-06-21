package org.beespoon.artemis.util.command.annotations.choices


/**
 * Register an array of [Double] choices.
 *
 * This annotation will automatically register the command choices set here.
 *
 * Example usage:
 * ```kotlin
 * fun myCommand(
 *     event: // Command event
 *
 *     @CommandOption
 *     @DoubleChoices([0.00,0.01,0.02])
 *     channel: Channel
 * ) {}
 * ```
 * @property choices Array of [Double].
 * @author <a href="https://www.bspoones.com">BSpoones</a>
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class DoubleChoices(
    val choices: DoubleArray = [],
)
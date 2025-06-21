package org.beespoon.artemis.util.command.annotations.choices

/**
 * Register an array of [String] choices.
 *
 * This annotation will automatically register the command choices set here.
 *
 * Example usage:
 * ```kotlin
 * fun myCommand(
 *     event: // Command event
 *
 *     @CommandOption
 *     @DoubleChoices(["One", "Two", "Three"])
 *     channel: Channel
 * ) {}
 * ```
 * @property choices Array of [String].
 * @author <a href="https://www.bspoones.com">BSpoones</a>
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class StringChoices(
    val choices: Array<String>
)
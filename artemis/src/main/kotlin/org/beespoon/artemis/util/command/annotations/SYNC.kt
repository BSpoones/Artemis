package org.beespoon.artemis.util.command.annotations

/**
 * Set a command to be run synchronously. By default, all commands are run asynchronously
 *
 * Example usage:
 * ```kotlin
 * @SYNC
 * @SlashCommand("name","description") // All other command types work
 * fun myCommand(
 *     event: SlashCommandInteractionEvent
 * ) {}
 * ```
 * @author <a href="https://www.bspoones.com">BSpoones</a>
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class SYNC
package org.beespoon.artemis.util.command.annotations

/**
 * Set a command to be NSFW
 *
 * This annotation will only allow this command to be run in NSFW text channels
 *
 * Example usage:
 * ```kotlin
 * @SlashCommand("name","description") // All other command types work
 * @NSFW
 * fun myCommand(
 *     event: SlashCommandInteractionEvent
 * ) {}
 * ```
 * @author <a href="https://www.bspoones.com">BSpoones</a>
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class NSFW
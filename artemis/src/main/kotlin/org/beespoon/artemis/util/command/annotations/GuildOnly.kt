package org.beespoon.artemis.util.command.annotations


/**
 * Set a command to be guild only
 *
 * This annotation will only register this command in the `guilds` variable set in Zeus
 *
 * Example usage:
 * ```kotlin
 * @SlashCommand("name","description") // All other command types work
 * @GuildOnly
 * fun myCommand(
 *     event: SlashCommandInteractionEvent
 * ) {}
 * ```
 * @author <a href="https://www.bspoones.com">BSpoones</a>
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class GuildOnly
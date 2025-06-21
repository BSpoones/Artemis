package org.beespoon.artemis.util.command.annotations.command

/**
 * Registers a slash command
 *
 * This annotation will automatically register the command set here.
 *
 * Example usage:
 * ```kotlin
 * @SlashCommand("name","description")
 * fun myCommand(
 *     event: SlashCommandInteractionEvent
 * ) {}
 * ```
 * @property name  [String]: Command name. LOWERCASE ONLY
 * @property description [String]: Command description.
 * @author <a href="https://www.bspoones.com">BSpoones</a>
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class SlashCommand(val name: String, val description: String = " ")
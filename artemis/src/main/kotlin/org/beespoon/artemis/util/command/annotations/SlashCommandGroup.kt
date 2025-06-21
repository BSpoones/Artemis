package org.beespoon.artemis.util.command.annotations

/**
 * Create a slash command group via an object
 *
 * This annotation will turn an object into a slash command group
 *
 * Example slash group creation:
 * ```kotlin
 * @SlashCommandGroup("group", "Slash command group example")
 * object ExampleGroup: Command() {
 *
 *     @SlashCommand("subcommand","description") // Becomes /group subcommand
 *     fun myCommand(
 *         event: SlashCommandInteractionEvent
 *     ) {}
 * }
 * ```
 *
 * Example nested slash group creation:
 * **NOTE: Discord API only allows for a 2-level nested group**
 * ```kotlin
 * @SlashCommandGroup("group", "Slash command group")
 * object ExampleGroup: Command() {
 *
 *     @SlashCommandGroup("subgroup", "Slash command subgroup)
 *     object ExampleSubGroup {
 *
 *         @SlashCommand("command","description") // Becomes /group subgroup command
 *         fun myCommand(
 *             event: SlashCommandInteractionEvent
 *         ) {}
 *     }
 * }
 * ```
 * @property name  [String]: Group name. LOWERCASE ONLY
 * @property description  [String]: Group description
 * @author <a href="https://www.bspoones.com">BSpoones</a>
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class SlashCommandGroup(val name: String, val description: String = " ")
package org.beespoon.artemis.util.command.annotations

/**
 *
 * Registers a command option
 *
 * This annotation will register an option for the selected parameter
 *
 * Example usage:
 * ```kotlin
 * @SlashCommand("name","description") // All other command types work
 * fun myCommand(
 *     event: SlashCommandInteractionEvent
 *
 *     @CommandOption("example","Example String slash option")
 * ) {}
 * ```
 * @property name [String]: Option name. **LOWERCASE ONLY**
 * @property description [String]: Option Description
 * @property isRequired [Boolean]: Is the option required? Default = true
 * @property autoComplete [Boolean]: Should a choice annotation be AutoComplete? Default = false
 * @see org.beespoon.artemis.util.command.annotations.choices
 * @author <a href="https://www.bspoones.com">BSpoones</a>
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class CommandOption(
    val name: String,
    val description: String = " ",
    val isRequired: Boolean = true,
    val autoComplete: Boolean = false
)

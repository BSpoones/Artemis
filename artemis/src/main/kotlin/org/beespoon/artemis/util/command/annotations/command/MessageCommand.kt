package org.beespoon.artemis.util.command.annotations.command

/**
 * Registers a message command
 *
 * This annotation will automatically register the command set here.
 *
 * If CommandRegistry.prefixGuildMap doesn't contain a guild's selected
 * message prefix, the global prefix (default to "!") will be used.
 *
 * Example usage:
 * ```kotlin
 * @MessageCommand("name","description")
 * fun myCommand(
 *     event: MessageReceivedEvent
 * ) {}
 * ```
 * @property name -> [String]: Command name.
 * @property description -> [String]: Command description.
 * @author <a href="https://www.bspoones.com">BSpoones</a>
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class MessageCommand(val name: String, val description: String = " ")
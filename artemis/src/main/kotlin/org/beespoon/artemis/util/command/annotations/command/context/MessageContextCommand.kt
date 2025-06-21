package org.beespoon.artemis.util.command.annotations.command.context

/**
 * Registers a message context command. Right-click a message -> Apps to
 * find this command!
 *
 * This annotation will automatically register the command set here.
 *
 * Example usage:
 * ```kotlin
 * @MessageContextCommand("name")
 * fun myCommand(
 *     event: MessageContextInteractionEvent
 * ) {}
 * ```
 * @property name -> [String]: Command name.
 * @author <a href="https://www.bspoones.com">BSpoones</a>
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class MessageContextCommand(val name: String)
package org.beespoon.artemis.util.command.annotations.command.context

/**
 * Registers a user context command. Right-click a user -> Apps to
 * find this command!
 *
 * This annotation will automatically register the command set here.
 *
 *
 * Example usage:
 * ```kotlin
 * @UserContextCommand("name")
 * fun myCommand(
 *     event: UserContextInteractionEvent
 * ) {}
 * ```
 * @property name -> [String]: Command name.
 * @author <a href="https://www.bspoones.com">BSpoones</a>
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class UserContextCommand(val name: String)
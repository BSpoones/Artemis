package org.beespoon.artemis.util.command.annotations.permission

import net.dv8tion.jda.api.Permission

/**
 * Sets a command permission
 *
 * This annotation will handle permission handling for multiple permissions,
 * and only run the function if the user has the correct permissions
 *
 * Example usage:
 * ```kotlin
 * @SlashCommand("name","description") // Works on all other commands too
 * @Permissions([Permission.ADMINISTRATOR, PERMISSION.MANAGE_SERVER])
 * fun myCommand(
 *     event: SlashCommandInteractionEvent
 * ) {}
 * ```
 * @property permissions Array<[Permission]>: Array of Permissions
 * @author <a href="https://www.bspoones.com">BSpoones</a>
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class Permissions(
    val permissions: Array<Permission>
)
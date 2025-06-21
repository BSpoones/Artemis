package org.beespoon.artemis.util.command.annotations.permission

import net.dv8tion.jda.api.Permission

/**
 * Sets a command permission
 *
 * This annotation will handle permission handling for a single permission,
 * and only run the function if the user has the correct permissions
 *
 * Example usage:
 * ```kotlin
 * @SlashCommand("name","description") // Works on all other commands too
 * @Permission(Permission.ADMINISTRATOR)
 * fun myCommand(
 *     event: SlashCommandInteractionEvent
 * ) {}
 * ```
 * @property permission [Permission]: Permission
 * @author <a href="https://www.bspoones.com">BSpoones</a>
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class Permission(
    val permission: Permission
)
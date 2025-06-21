package org.beespoon.artemis.util.command.handler

import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.Permission as JDAPermission
import org.beespoon.artemis.util.command.annotations.permission.Permission
import org.beespoon.artemis.util.command.annotations.permission.Permissions
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation

/**
 * Permission builder for all commands
 *
 * Registers command permissions
 * @see org.beespoon.artemis.util.command.annotations.permission
 * @see org.beespoon.artemis.util.command.annotations.permission
 * @author <a href="https://www.bspoones.com">BSpoones</a>
 */
internal object PermissionHandler {

    /**
     * Command permission builder
     *
     * @param method [KFunction] - Method (Command) to set permissions for
     * @see org.beespoon.artemis.util.command.annotations.permission
     * @author <a href="https://www.bspoones.com">BSpoones</a>
     */
    fun buildPermissions(method: KFunction<*>): DefaultMemberPermissions {
        val permission = method.findAnnotation<Permission>()?.permission
        val permissions = method.findAnnotation<Permissions>()?.permissions
        return buildPermissions(permission, permissions)
    }

    /**
     * Group permission builder
     *
     * @param clazz [KClass] - Object (Group) to set permissions for
     * @see org.beespoon.artemis.util.command.annotations.permission
     * @author <a href="https://www.bspoones.com">BSpoones</a>
     */
    fun buildPermissions(clazz: KClass<*>): DefaultMemberPermissions {
        val permission = clazz.findAnnotation<Permission>()?.permission
        val permissions = clazz.findAnnotation<Permissions>()?.permissions
        return buildPermissions(permission, permissions)
    }

    /**
     * Creates a [DefaultMemberPermissions] instance from the annotation permission(s)
     *
     * @param permission [JDAPermission] - Single permission, if any
     * @param permissions Array<[JDAPermission]> - Permission aray, if exists
     * @return [DefaultMemberPermissions] - Member permissions instance
     * @see org.beespoon.artemis.util.command.annotations.permission
     * @author <a href="https://www.bspoones.com">BSpoones</a>
     */
    private fun buildPermissions(permission: JDAPermission?, permissions: Array<JDAPermission>?): DefaultMemberPermissions {
        val allPermissions = (permissions ?: emptyArray()).toMutableList()
        permission?.let { allPermissions.add(it) }
        return DefaultMemberPermissions.enabledFor(allPermissions)
    }
}
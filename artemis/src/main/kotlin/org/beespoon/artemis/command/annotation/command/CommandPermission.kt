package org.beespoon.artemis.command.annotation.command

import net.dv8tion.jda.api.Permission

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class CommandPermission(vararg val permissions: Permission)
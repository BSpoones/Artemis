package org.beespoon.artemis.command.annotation.command.type

import org.beespoon.artemis.command.annotation.command.CommandInformation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class UserContextCommand(val info: CommandInformation)
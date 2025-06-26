package org.beespoon.artemis.command.annotation.command

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class CommandOption(
    val name: String,
    val description: String = "",
    val isRequired: Boolean = true,
    val autoComplete: Boolean = false
)

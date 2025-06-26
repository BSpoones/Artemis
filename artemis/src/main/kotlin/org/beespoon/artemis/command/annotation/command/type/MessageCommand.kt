package org.beespoon.artemis.command.annotation.command.type

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class MessageCommand(
    val name: String,
    val description: String = "",
    val usage: String = ""
)
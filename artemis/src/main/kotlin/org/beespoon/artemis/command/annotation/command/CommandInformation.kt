package org.beespoon.artemis.command.annotation.command

annotation class CommandInformation(
    val name: String,
    val description: String = "",
    val usage: String = ""
)

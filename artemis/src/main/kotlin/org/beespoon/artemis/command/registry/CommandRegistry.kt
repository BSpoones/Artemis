package org.beespoon.artemis.command.registry

import net.dv8tion.jda.api.interactions.commands.Command
import org.beespoon.artemis.command.ArtemisCommands
import org.beespoon.artemis.command.annotation.command.type.MessageCommand
import org.beespoon.artemis.command.annotation.command.type.MessageContextCommand
import org.beespoon.artemis.command.annotation.command.type.SlashCommand
import org.beespoon.artemis.command.annotation.command.type.UserContextCommand
import org.slf4j.LoggerFactory
import kotlin.reflect.KFunction
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberFunctions

object CommandRegistry {

    private val logger = LoggerFactory.getLogger("Artemis | Command Registry")

    private val commandRegistry: MutableMap<Command.Type, MutableList<CommandNode>> =
        Command.Type.entries.associateWith { mutableListOf<CommandNode>() }.toMutableMap()

    val autoCompleteMap: MutableMap<String, Map<String, List<Any>>> = mutableMapOf()


    // Registers a command object
    fun <T : ArtemisCommands> registerCommands(clazz: T) {
        clazz::class.memberFunctions.forEach { function ->
            registerFunction(function)
        }
    }

    private fun registerFunction(function: KFunction<*>) {
        val types = getCommandTypes(function)
        if (types.isEmpty()) return // Non-command func

        val options


    }


    // Gets all command types from a method
    private fun getCommandTypes(function: KFunction<*>): Set<Command.Type> {
        return mutableMapOf(
            Command.Type.SLASH to function.hasAnnotation<SlashCommand>(),
            Command.Type.UNKNOWN to function.hasAnnotation<MessageCommand>(),
            Command.Type.MESSAGE to function.hasAnnotation<MessageContextCommand>(),
            Command.Type.USER to function.hasAnnotation<UserContextCommand>(),
        ).filterValues { it }.keys
    }
}
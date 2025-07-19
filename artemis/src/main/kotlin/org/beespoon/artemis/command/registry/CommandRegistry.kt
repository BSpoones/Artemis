package org.beespoon.artemis.command.registry

import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import org.beespoon.artemis.command.ArtemisCommand
import org.beespoon.artemis.command.annotation.command.CommandInformation
import org.beespoon.artemis.command.annotation.command.type.MessageCommand
import org.beespoon.artemis.command.annotation.command.type.MessageContextCommand
import org.beespoon.artemis.command.annotation.command.type.SlashCommand
import org.beespoon.artemis.command.annotation.command.type.UserContextCommand
import org.slf4j.LoggerFactory
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberFunctions

object CommandRegistry {

    private val logger = LoggerFactory.getLogger("Artemis | Command Registry")

    private val commandRegistry: MutableMap<Command.Type, MutableList<CommandNode>> =
        Command.Type.entries.associateWith { mutableListOf<CommandNode>() }.toMutableMap()

    val autoCompleteMap: MutableMap<String, Map<String, List<Any>>> = mutableMapOf()


    // Registers a command object
    fun <T : ArtemisCommand> registerCommands(clazz: T) {
        clazz::class.memberFunctions.forEach { function ->
            registerFunction(function)
        }
    }

    private fun registerFunction(function: KFunction<*>) {
        val types = getCommandTypes(function)
        if (types.isEmpty()) return // Non-command func

        types.forEach { commandType ->
            val registry = getCommandRegistry(commandType)
            val info = getCommandInfo(function, commandType)
            val parentNode = searchNodeTree(registry, info.name)

            val nodeName = if (parentNode != null) {
                info.name.removePrefix("${parentNode.fullName} ").trim()
            } else info.name

            if (parentNode?.children?.any { it.name.equals(nodeName, true) } == true) {
                throw IllegalArgumentException("A ${commandType.name} command already exists with name: ${info.name}")
            }

            val commandNode = CommandNode(
                nodeName,
                info.name,
                info.description,
                info.usage,
                function,
                getCommandData(function, parentNode, commandType, info),
                mutableListOf()
            )

            if (parentNode == null) {
                registry.add(commandNode)
            } else {
                parentNode.children.add(commandNode)
            }
        }
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

    private fun getCommandInfo(function: KFunction<*>, type: Command.Type): CommandInformation {
        return when (type) {
            Command.Type.SLASH -> function.findAnnotation<SlashCommand>()?.info
            Command.Type.UNKNOWN -> function.findAnnotation<MessageCommand>()?.info
            Command.Type.MESSAGE -> function.findAnnotation<MessageContextCommand>()?.info
            Command.Type.USER -> function.findAnnotation<UserContextCommand>()?.info
        }
            ?: throw IllegalArgumentException("Command information not found on ${function.javaClass.name}-${function.name}")
    }

    // Searches through command node tree until a node is found
    private fun searchNodeTree(registry: MutableList<CommandNode>, commandName: String): CommandNode? {
        val splitName = commandName.split(" ")
        if (splitName.size > 3) {
            throw IllegalArgumentException("You may only have two sub-commands in a command!")
        }

        var currentNode = registry.find { it.name == splitName.first() } ?: return null

        for (name in splitName.drop(1)) {
            val nextNode = currentNode.children.find { it.name == name } ?: break
            currentNode = nextNode
        }

        return currentNode
    }

    private fun getCommandRegistry(type: Command.Type): MutableList<CommandNode> {
        var nodes = commandRegistry[type]
        if (nodes == null) {
            nodes = mutableListOf()
            commandRegistry[type] = nodes
        }
        return nodes
    }

    private fun getCommandData(
        function: KFunction<*>,
        parentNode: CommandNode?,
        type: Command.Type,
        info: CommandInformation
    ): CommandData {
        // TODO
    }

}
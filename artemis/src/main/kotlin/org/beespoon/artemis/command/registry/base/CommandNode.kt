package org.beespoon.artemis.command.registry.base

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import org.beespoon.artemis.command.annotation.command.CommandOption
import org.beespoon.artemis.util.extensions.getOptionValue
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

data class CommandNode(
    val name: String,
    val fullName: String,
    val description: String?,
    val usage: String?,
    val method: KFunction<*>,
    val command: CommandData,
    val children: MutableList<CommandNode>
) {

    fun getSlashArgs(event: SlashCommandInteractionEvent): Array<Any?> {
        return method.parameters
            .filter { it.hasAnnotation<CommandOption>() }
            .map { param ->
                val optionAnnotation = param.findAnnotation<CommandOption>()!!
                event.getOption(optionAnnotation.name)?.getOptionValue(param.type)
            }
            .toTypedArray()
    }

    fun getMessageArgs(command: String): Array<Any?> {
        // TODO
        return arrayOf()
    }
}
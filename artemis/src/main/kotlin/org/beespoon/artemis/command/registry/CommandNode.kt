package org.beespoon.artemis.command.registry

import net.dv8tion.jda.api.entities.IMentionable
import net.dv8tion.jda.api.entities.Message.Attachment
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.Channel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.beespoon.artemis.command.annotation.command.CommandOption
import org.beespoon.artemis.util.extensions.*
import org.beespoon.artemis.util.extensions.getOptionValue
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

data class CommandNode(
    val name: String,
    val fullName: String,
    val description: String?,
    val usage: String?,
    val method: KFunction<*>,
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

    }


    fun getMessageOptions(arg: String, type: KType, attachment: Attachment? = null): Any? {
        return when (type.classifier) {
            String::class -> arg
            Int::class -> arg.toIntOrNull()
            Long::class -> arg.toIntOrNull()
            Double::class -> arg.toDoubleOrNull()
            Boolean::class -> arg.toBooleanStrictOrNull()
            User::class.java -> {
                if (arg.isUserPing()) {
                    val id = arg.numbersOnly() ?: return null
                    // TODO -> API Access
//                    CommandRegistry.api.getUserById(id.toLong())
                    null
                } else {
                    null
                }
            }
            Channel::class.java -> {
                if (arg.isChannelPing()) {
                    val id = arg.numbersOnly() ?: return null
                    // TODO -> API Access
//                    CommandRegistry.api.getChannelById((parameterType as Channel)::class.java, id.toLong())
                    null
                } else {
                    null
                }
            }
            Role::class.java -> {
                if (arg.isRolePing()) {
                    val id = arg.numbersOnly() ?: return null
                    // TODO -> API Access
//                    CommandRegistry.api.getRoleById(id.toLong())
                    null
                } else {
                    null
                }
            }
            IMentionable::class.java -> {
                val roleMatch = ROLE_PING_REGEX.matches(arg)
                val userMatch = USER_PING_REGEX.matches(arg)
                if (!arg.isRolePing() && !arg.isUserPing()) null
                else {
                    val id = arg.numbersOnly() ?: return null
                    // TODO -> API Access
//                    if (roleMatch) CommandRegistry.api.getRoleById(id.toLong())
//                    else  CommandRegistry.api.getUserById(id.toLong())
                }
            }
            Attachment::class.java -> {
                attachment
            }
            else -> null // Handle other types as needed
        }
    }
}
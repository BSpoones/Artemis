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
        val api = JdaApi.api()
        val id = arg.numbersOnly()?.toLongOrNull()
        return when (type.classifier) {
            Attachment::class.java -> attachment
            String::class -> arg
            Int::class -> arg.toIntOrNull()
            Long::class -> arg.toIntOrNull()
            Double::class -> arg.toDoubleOrNull()
            Boolean::class -> arg.toBooleanStrictOrNull()
            User::class.java -> if (arg.isUserPing() && id != null) api.getUserById(id) else null
            Role::class.java -> if (arg.isRolePing() && id != null) api.getRoleById(id) else null
            Channel::class.java -> if (arg.isChannelPing() && id != null)
                api.getChannelById((type as Channel)::class.java, id) else null

            IMentionable::class.java -> when {
                arg.isRolePing() && id != null -> api.getRoleById(id)
                arg.isUserPing() && id != null -> api.getUserById(id)
                else -> null
            }

            else -> null // Handle other types as needed
        }
    }
}
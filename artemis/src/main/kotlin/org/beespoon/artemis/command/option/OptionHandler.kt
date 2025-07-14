package org.beespoon.artemis.command.option

import net.dv8tion.jda.api.entities.IMentionable
import net.dv8tion.jda.api.entities.Message.Attachment
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.Channel
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import org.beespoon.artemis.api.JdaApi
import org.beespoon.artemis.command.annotation.choice.static.ChannelTypeChoices
import org.beespoon.artemis.command.annotation.command.CommandOption
import org.beespoon.artemis.command.registry.CommandRegistry
import org.beespoon.artemis.util.extensions.isChannelPing
import org.beespoon.artemis.util.extensions.isRolePing
import org.beespoon.artemis.util.extensions.isUserPing
import org.beespoon.artemis.util.extensions.numbersOnly
import org.beespoon.artemis.util.extensions.optionType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation

internal object OptionHandler {
    private val logger: Logger = LoggerFactory.getLogger("Artemis | Option Handler")

    fun buildOptions(method: KFunction<*>, commandName: String): List<OptionData> {
        this.logger.debug("Building options for $commandName")
        val options = mutableListOf<OptionData>()
        method.parameters.forEach { parameter ->
            val commandOption = parameter.findAnnotation<CommandOption>() ?: return@forEach
            val optionType = parameter.type.optionType()
                ?: throw IllegalArgumentException("${parameter.name} has an invalid parameter type: ${parameter.type}")

            val optionData =
                OptionData(optionType, commandOption.name, commandOption.description, commandOption.isRequired, commandOption.autoComplete)

            /**
             * JDA doesn't have an easy way of doing this passively, so they have to be added
             * in different ways
             */
            if (commandOption.autoComplete) {
                val optionChoice = CommandRegistry.autoCompleteMap.getOrDefault(commandName, mapOf()).toMutableMap()
                optionChoice[commandOption.name] = ChoiceHandler.getChoices(parameter)
                CommandRegistry.autoCompleteMap[commandName] = optionChoice
            } else {
                optionData.addChoices(ChoiceHandler.buildChoices(parameter))
            }

            // Sets channel types if option value is ChannelType
            val channelTypesAnnotation = parameter.findAnnotation<ChannelTypeChoices>()
            if (channelTypesAnnotation != null) {
                optionData.setChannelTypes(channelTypesAnnotation.choices.toList())
            }

            options.add(optionData)
        }
        this.logger.debug("${options.size} options added to $commandName")
        return options
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
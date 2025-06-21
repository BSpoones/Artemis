package org.beespoon.artemis.util.command.handler

import net.dv8tion.jda.api.entities.IMentionable
import net.dv8tion.jda.api.entities.Message.Attachment
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.Channel
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import org.beespoon.artemis.Artemis
import org.beespoon.artemis.util.command.annotations.CommandOption
import org.beespoon.artemis.util.command.annotations.choices.ChannelTypes
import org.beespoon.artemis.util.command.CommandRegistry
import org.bspoones.zeus.extensions.optionType
import org.bspoones.zeus.logging.ZeusLogger
import org.bspoones.zeus.logging.getZeusLogger
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation

/**
 * Regex constants
 */
val USER_PING_REGEX = "<@!?(\\d+)>".toRegex()
val ROLE_PING_REGEX = "<@&!?(\\d+)>".toRegex()
val CHANNEL_PING_REGEX = "<#!?(\\d+)>".toRegex()
val NUMBER_ONLY_REGEX = "\\d+".toRegex()

/**
 * Command option builder
 *
 * @see org.beespoon.artemis.util.command.annotations.CommandOption
 * @author <a href="https://www.bspoones.com">BSpoones</a>
 */
internal object OptionHandler {

    /**
     * Registers options for slash / message commands
     *
     * @param method [KFunction] - Method (Command) being registered
     * @param commandName [String] - Command name
     * @see org.beespoon.artemis.util.command.annotations.CommandOption
     */
    fun buildOptions(method: KFunction<*>, commandName: String): List<OptionData> {
        Artemis.logger.debug("Building options for $commandName")
        val options = mutableListOf<OptionData>()
        method.parameters.forEach { parameter ->
            val commandOption = parameter.findAnnotation<CommandOption>() ?: return@forEach
            val optionType = parameter.type.optionType()
                ?: throw IllegalArgumentException("${parameter.name} has an invalid parameter type: ${parameter.type}")

            val optionData =
                OptionData(
                    optionType,
                    commandOption.name,
                    commandOption.description,
                    commandOption.isRequired,
                    commandOption.autoComplete
                )

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
            val channelTypesAnnotation = parameter.findAnnotation<ChannelTypes>()
            if (channelTypesAnnotation != null) {
                optionData.setChannelTypes(channelTypesAnnotation.channelTypes.toList())
            }

            options.add(optionData)
        }
        Artemis.logger.debug("${options.size} options added to $commandName")
        return options
    }


    /**
     * Registers a message command option via [String] checks
     *
     * @param arg [String] - Message command arg
     * @param parameterType [KType] - Expected parameter type
     * @param attachment [Attachment] - Message attachment (if any)
     * @return [Any] - Return value
     * @author <a href="https://www.bspoones.com">BSpoones</a>
     */
    fun getMessageOption(arg: String, parameterType: KType, attachment: Attachment? = null): Any? {
        return when (parameterType) {
            String::class.java -> arg
            Int::class.java -> arg.toInt()
            Boolean::class.java -> arg.toBoolean()
            Double::class.java -> arg.toDouble()
            User::class.java -> {
                if (!USER_PING_REGEX.matches(arg)) null
                else {
                    val id = NUMBER_ONLY_REGEX.find(arg)?.value ?: return null
                    CommandRegistry.api.getUserById(id.toLong())
                }
            }

            Channel::class.java -> {
                if (!CHANNEL_PING_REGEX.matches(arg)) null
                else {
                    val id = NUMBER_ONLY_REGEX.find(arg)?.value ?: return null
                    CommandRegistry.api.getChannelById((parameterType as Channel)::class.java, id.toLong())
                }
            }

            Role::class.java -> {
                if (!ROLE_PING_REGEX.matches(arg)) null
                else {
                    val id = NUMBER_ONLY_REGEX.find(arg)?.value ?: return null
                    CommandRegistry.api.getRoleById(id.toLong())
                }
            }

            IMentionable::class.java -> {
                val roleMatch = ROLE_PING_REGEX.matches(arg)
                val userMatch = USER_PING_REGEX.matches(arg)
                if (!roleMatch && !userMatch) null
                else {
                    val id = NUMBER_ONLY_REGEX.find(arg)?.value ?: return null
                    if (roleMatch) CommandRegistry.api.getRoleById(id.toLong())
                    else CommandRegistry.api.getUserById(id.toLong())
                }
            }

            Attachment::class.java -> {
                if (attachment == null) return null
                attachment
            }

            else -> null
        }
    }
}
package org.beespoon.artemis.util.command

import net.dv8tion.jda.api.entities.Message.Attachment
import net.dv8tion.jda.api.events.interaction.command.*
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.Command
import org.beespoon.artemis.util.command.annotations.CommandOption
import org.beespoon.artemis.util.command.annotations.SYNC
import org.beespoon.artemis.util.command.tree.CommandForest
import org.beespoon.artemis.util.command.enums.CommandType
import org.beespoon.artemis.util.command.enums.toCommandType
import org.beespoon.artemis.util.command.handler.NsfwHandler
import org.beespoon.artemis.util.command.handler.OptionHandler
import org.bspoones.zeus.extensions.getOptionValue
import org.bspoones.zeus.logging.getZeusLogger
import org.bspoones.zeus.util.scheduling.async
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.jvm.javaMethod

/**
 * Command class
 *
 * Open class used to register all groups, commands, options, and choices
 * within an object, including the object itself as well as run them
 *
 * ```kotlin
 * object MyCommand: Command() {
 *     @SlashCommand("name","description")
 *     fun onName(
 *         event: SlashCommandInteractionEvent
 *     )
 * }
 * ```
 *
 * @see org.beespoon.artemis.util.command.annotations.command.SlashCommand
 * @see org.beespoon.artemis.util.command.annotations.command.MessageCommand
 * @see org.beespoon.artemis.util.command.annotations.command.context.UserContextCommand
 * @see org.beespoon.artemis.util.command.annotations.command.context.MessageContextCommand
 *
 * @author <a href="https://www.bspoones.com">BSpoones</a>
 */
open class Command : ListenerAdapter() {

    private val logger = getZeusLogger("Command")

    /**
     * Slash command interaction listener
     *
     * Finds a slash command method in the command forest by the fullCommandName
     * If present, it calls the command
     *
     * @see org.beespoon.artemis.util.command.annotations.command.SlashCommand
     * @author <a href="https://www.bspoones.com">BSpoones</a>
     */
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if (tryRunPetal(event)) return

        val function = CommandForest.getFunction(CommandType.SLASH, event.fullCommandName) ?: return
        val functionObj = function.javaMethod?.declaringClass?.kotlin?.objectInstance ?: return

        if (NsfwHandler.nsfwCheck(function, event)) return

        /**
         * Option handler
         *
         * @author <a href="https://www.bspoones.com">BSpoones</a>
         */
        val args = mutableListOf<Any?>()
        val sync = function.hasAnnotation<SYNC>()
        function.parameters.forEach { parameter ->
            val optionAnnotation = parameter.findAnnotation<CommandOption>() ?: return@forEach
            val value = event.getOption(optionAnnotation.name)
            if (value != null) {
                val optionValue = value.getOptionValue(parameter.type)
                    ?: throw IllegalArgumentException("Option type cannot be ${parameter.type}")
                args.add(optionValue)
            } else {
                // When an option is isRequired
                args.add(null)
            }
        }

        logger.info("SLASH - @${event.user.name} in ${if (event.isFromGuild) "${event.guild!!.name} (${event.guild!!.id})" else "their DM"}: ${event.commandString}")
        if (sync) {
            function.call(functionObj, event, *args.toTypedArray())
        } else {
            async {
                function.call(functionObj, event, *args.toTypedArray())
            }
        }
        return
    }

    /**
     * User context command interaction listener
     *
     * Finds a user context method in the command forest by the fullCommandName
     * If present, it calls the command
     *
     * @see org.beespoon.artemis.util.command.annotations.command.context.UserContextCommand
     * @author <a href="https://www.bspoones.com">BSpoones</a>
     */
    override fun onUserContextInteraction(event: UserContextInteractionEvent) {
        if (tryRunPetal(event)) return

        val function = CommandForest.getFunction(CommandType.USER_CONTEXT, event.fullCommandName) ?: return
        val functionObj = function.javaMethod?.declaringClass?.kotlin?.objectInstance ?: return
        if (NsfwHandler.nsfwCheck(function, event)) return
        if (function.hasAnnotation<SYNC>()) {

            logger.info("USER CONTEXT - @${event.user.name} in ${if (event.isFromGuild) "${event.guild!!.name} (${event.guild!!.id})" else "their DM"}: ${event.commandString}")
            function.call(functionObj, event)
        } else {
            async {
                function.call(functionObj, event)
            }
        }
    }

    /**
     * Message context command interaction listener
     *
     * Finds a message context method in the command forest by the fullCommandName
     * If present, it calls the command
     *
     * @see org.beespoon.artemis.util.command.annotations.command.context.MessageContextCommand
     * @author <a href="https://www.bspoones.com">BSpoones</a>
     */
    override fun onMessageContextInteraction(event: MessageContextInteractionEvent) {
        if (tryRunPetal(event)) return

        val function = CommandForest.getFunction(CommandType.MESSAGE_CONTEXT, event.fullCommandName) ?: return
        val functionObj = function.javaMethod?.declaringClass?.kotlin?.objectInstance ?: return
        if (NsfwHandler.nsfwCheck(function, event)) return

        logger.info("MESSAGE CONTEXT - @${event.user.name} in ${if (event.isFromGuild) "${event.guild!!.name} (${event.guild!!.id})" else "their DM"}: ${event.commandString}")
        if (function.hasAnnotation<SYNC>()) {
            function.call(functionObj, event)
        } else {
            async {
                function.call(functionObj, event)
            }
        }
    }

    /**
     * Message command interaction listener
     *
     * Finds a user context method in the command forest by the fullCommandName
     * If present, it calls the command
     *
     * **NOTE: There are still some issues with default values**
     *
     * @see org.beespoon.artemis.util.command.annotations.command.MessageCommand
     * @author <a href="https://www.bspoones.com">BSpoones</a>
     */
    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot) return
        val content = event.message.contentRaw
        if (!content.startsWith(CommandRegistry.getPrefix(event.guild.idLong))) return

        val contentArgs = content.split(" ").filter { it != "" }
        val commandName = contentArgs.first().substring(1)

        val function = CommandForest.getFunction(CommandType.MESSAGE, commandName) ?: return
        val functionObj = function.javaMethod?.declaringClass?.kotlin?.objectInstance ?: return
        if (NsfwHandler.nsfwCheck(function, event)) return

        // Drops event
        val args: MutableList<String?> = contentArgs.drop(1).toMutableList()
        val funcArgs = mutableListOf<Any>()

        var attachmentIndex = 0 // Ensures attachments are added correctly
        val sync = function.hasAnnotation<SYNC>()
        function.parameters.drop(1).forEachIndexed { index, parameter ->
            val optionAnnotation = parameter.findAnnotation<CommandOption>() ?: return@forEachIndexed

            if (parameter.type::class.java == Attachment::class.java) attachmentIndex += 1

            val value = args.getOrNull(index - 1)
            if (value == null && parameter.isOptional) return@forEachIndexed
            if (value == null && !parameter.isOptional) {
                event.channel.sendMessage("<@${event.author.id}> Argument not provided: ${optionAnnotation.name}")
                    .setMessageReference(event.messageId).queue()
                return
            }

            /**
             * Option handling
             *
             * @see org.beespoon.artemis.util.command.handler.OptionHandler
             */
            val optionValue = OptionHandler.getMessageOption(
                value!!,
                parameter.type,
                event.message.attachments.getOrNull(attachmentIndex)
            )
                ?: {
                    event.channel.sendMessage("<@${event.author.id}> Invalid selection for `${optionAnnotation.name}`. It must be of type ${parameter.type}")
                        .setMessageReference(event.messageId).queue()
                }
            funcArgs.add(optionValue)
        }

        // Argument size check - Ensures all required options are given in message commands
        val minSize = function.parameters.size - 2 - function.parameters.filter { it.isOptional }.size
        if (args.size < minSize) {
            event.channel.sendMessage("<@${event.author.id}> Invalid arguments: ${args.joinToString(" ")}")
                .setMessageReference(event.messageId).queue()
            return
        }
        // TODO -> Make this work
//        args.addAll(List(function.parameters.filter { it.isOptional }.size - args.size) { null })

        logger.info("MESSAGE - @${event.author.name} in ${if (event.isFromGuild) "${event.guild.name} (${event.guild.id})" else "their DM"}: ${event.message}")
        if (sync) {
            function.call(functionObj, event, *args.toTypedArray())
        } else {
            async {
                function.call(functionObj, event, *args.toTypedArray())
            }
        }
    }

    /**
     * AutoComplete handler
     *
     * Handles AutoComplete interactions by finding the command parameter's autocomplete data, if present
     *
     * @see org.beespoon.artemis.util.command.handler.OptionHandler.getMessageOption
     * @author <a href="https://www.bspoones.com">BSpoones</a>
     */
    @Suppress("UNCHECKED_CAST")
    override fun onCommandAutoCompleteInteraction(event: CommandAutoCompleteInteractionEvent) {
        val options = CommandRegistry.autoCompleteMap[event.fullCommandName] ?: return
        var choices = options[event.focusedOption.name] ?: return
        if (choices.isEmpty()) return

        choices = when (choices.first()) {
            is String -> choices as List<String>
            is Double -> choices as List<Double>
            is Long -> choices as List<Long>
            else -> return
        }
        event.replyChoiceStrings(
            (choices)
                .filter { it.toString().startsWith(event.focusedOption.value, ignoreCase = true) }
                    as List<String>
        ).queue()
    }

    private fun tryRunPetal(event: GenericCommandInteractionEvent): Boolean {
        if (event.guild != null) {
            val configCommand = CommandRegistry.findConfigCommand(event.guild!!.idLong, event.name)
            if (configCommand != null) {
                configCommand.run(event)
                return true
            }
        }
        return false
    }
}
package org.beespoon.artemis.util.command.handler

import net.dv8tion.jda.api.entities.channel.Channel
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel
import net.dv8tion.jda.api.entities.channel.concrete.StageChannel
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.beespoon.artemis.util.command.annotations.NSFW
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.hasAnnotation

/**
 * Determining if an object or method is NSFW
 *
 * @see org.beespoon.artemis.util.command.annotations.NSFW
 * @author <a href="https://www.bspoones.com">BSpoones</a>
 */
internal object NsfwHandler {
    fun buildNsfw(method: KFunction<*>): Boolean {
        return method.hasAnnotation<NSFW>()
    }

    fun buildNsfw(clazz: KClass<*>): Boolean {
        return clazz.hasAnnotation<NSFW>()
    }

    /**
     * Checks if a NSFW command should run
     *
     * @param method [KFunction] - Method (Command) in question
     * @param channel [Channel] - Discord channel the command is being run in
     *
     * @return [Boolean] - True if command should not run
     * @author <a href="https://www.bspoones.com">BSpoones</a>
     */
    fun isNsfw(method: KFunction<*>, channel: Channel): Boolean {
        val nsfwChannel = when (channel.type) {
            ChannelType.TEXT -> (channel as TextChannel).isNSFW
            ChannelType.NEWS -> (channel as NewsChannel).isNSFW
            ChannelType.STAGE -> (channel as StageChannel).isNSFW
            else -> false
        }

        return !nsfwChannel && method.hasAnnotation<NSFW>()
    }

    /**
     * Checks if message command is NSFW
     *
     * @param method [KFunction] - Method (Command) in question
     * @param event [MessageReceivedEvent] - Message command event, already checked
     * @see org.beespoon.artemis.util.command.Command.onMessageReceived
     *
     * @return [Boolean] - True if command should not run
     * @author <a href="https://www.bspoones.com">BSpoones</a>
     */
    fun nsfwCheck(method: KFunction<*>, event: MessageReceivedEvent): Boolean {
        if (isNsfw(method, event.channel)) {
            event.channel.sendMessage("<@${event.author.id}> you cannot use an NSFW command in this channel!").setMessageReference(event.messageId).queue()
            return true
        }
        return false
    }

    /**
     * Checks if context command is NSFW
     *
     * @param method [KFunction] - Method (Command) in question
     * @param event [GenericCommandInteractionEvent] - Context interaction event
     * @see org.beespoon.artemis.util.command.Command.onUserContextInteraction
     * @see org.beespoon.artemis.util.command.Command.onMessageContextInteraction
     *
     * @return [Boolean] - True if command should not run
     * @author <a href="https://www.bspoones.com">BSpoones</a>
     */
    fun nsfwCheck(method: KFunction<*>, event: GenericCommandInteractionEvent) : Boolean{
        if (event.channel == null) return false

        if (isNsfw(method, event.channel!!)) {
            event.reply("This command can only be used in an NSFW channel!").setEphemeral(true).queue()
            return true
        }
        return false
    }

}
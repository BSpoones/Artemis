package org.beespoon.artemis.command.config

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class SerialisedCommand(
    var name: String,
    var description: String,
    var commandTypes: MutableList<Command.Type>,

    var permissions: MutableList<Permission>,
    var nsfw: Boolean,

    var passMember: Boolean,

    var message: String?,
    var embeds: MutableList<ConfigEmbed>


) {
    constructor() : this("", "", mutableListOf(), mutableListOf(), false, false, null, mutableListOf())

    fun run(event: GenericCommandInteractionEvent) {
        // TODO
        if (event.guild == null) return
        if (event.member == null) return
        if (!commandTypes.contains(event.commandType)) return

        val message = MessageCreateBuilder()
            .setContent(message)
            .setEmbeds(embeds.map {
                if (passMember) {
                    it.embed(event.member!!)
                } else {
                    it.embed()
                }
            })
            .build()

        event.reply(message).queue()
    }
}
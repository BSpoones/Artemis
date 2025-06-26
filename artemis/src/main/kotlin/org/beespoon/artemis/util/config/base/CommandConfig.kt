package org.beespoon.artemis.util.config.base

import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import org.beespoon.artemis.command.config.SerialisedCommand
import org.beespoon.artemis.util.scheduling.AsyncUtil
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import java.nio.file.WatchEvent
import java.time.Duration

@ConfigSerializable
abstract class CommandConfig : ActionableConfig() {
    abstract val guildIds: MutableList<Long>
    abstract val commands: MutableList<SerialisedCommand>

    fun commandData(): List<CommandData> {
        return commands.map { command ->
            val permissions = DefaultMemberPermissions.enabledFor(command.permissions)
            val nsfw = command.nsfw
            command.commandTypes.mapNotNull { commandType ->
                when (commandType) {
                    CommandType.SLASH -> {
                        Commands.slash(command.name, command.description)
                            .setDefaultPermissions(permissions)
                            .setNSFW(nsfw)
                    }

                    CommandType.MESSAGE -> {
                        null // Handled internally
                    }

                    CommandType.USER_CONTEXT -> {
                        Commands.context(Command.Type.USER, command.name)
                            .setDefaultPermissions(permissions)
                            .setNSFW(nsfw)
                    }

                    CommandType.MESSAGE_CONTEXT -> {
                        Commands.context(Command.Type.MESSAGE, command.name)
                            .setDefaultPermissions(permissions)
                            .setNSFW(nsfw)
                    }
                }
            }
        }.flatten()
    }

    override fun onChange(event: WatchEvent<*>) {
        getZeusLogger("CommandConfig").info(
            "Detected change in Command Config - ${this::class.simpleName}. Please wait up to a minute for commands to update!"
        )
        // Delaying to ensure config updates correctly
        AsyncUtil.delayedTask(Duration.ofSeconds(1)) {
            CommandRegistry.registerCommandConfig(this)
        }
    }

}
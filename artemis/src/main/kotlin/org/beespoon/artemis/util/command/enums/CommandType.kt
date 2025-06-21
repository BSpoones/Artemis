package org.beespoon.artemis.util.command.enums

import net.dv8tion.jda.api.interactions.commands.Command

/**
 * Command Type Enum
 * 
 * Used to help locate commands on the command tree
 * 
 * @see org.beespoon.artemis.util.command.tree
 * @author <a href="https://www.bspoones.com">BSpoones</a>
 */
enum class CommandType {
    SLASH,
    MESSAGE,
    USER_CONTEXT,
    MESSAGE_CONTEXT;

    fun toDiscord(): Command.Type {
        return when (this) {
             SLASH -> Command.Type.SLASH
             MESSAGE -> Command.Type.UNKNOWN
             USER_CONTEXT -> Command.Type.USER
             MESSAGE_CONTEXT -> Command.Type.MESSAGE
        }
    }
}

internal fun Command.Type.toCommandType(): CommandType {
    return when (this) {
        Command.Type.SLASH -> CommandType.SLASH
        Command.Type.UNKNOWN -> CommandType.MESSAGE
        Command.Type.USER -> CommandType.USER_CONTEXT
        Command.Type.MESSAGE -> CommandType.MESSAGE_CONTEXT
    }
}

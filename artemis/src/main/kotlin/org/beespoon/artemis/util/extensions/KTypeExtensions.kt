package org.beespoon.artemis.util.extensions

import net.dv8tion.jda.api.entities.IMentionable
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.Channel
import net.dv8tion.jda.api.interactions.commands.OptionType
import kotlin.reflect.KType
import kotlin.reflect.jvm.javaType

/**
 * Determines the OptionType corresponding to the KType.
 *
 * This function maps common Java types to their corresponding OptionType enum constants.
 * Supported types include STRING, INTEGER, BOOLEAN, NUMBER, USER, CHANNEL, ROLE, MENTIONABLE, and ATTACHMENT.
 * If no match is found, it returns null.
 *
 * @return The OptionType corresponding to the Java type of the KType, or null if no match is found.
 * @see OptionType
 * @author <a href="https://www.bspoones.com">BSpoones</a>
 */
internal fun KType.optionType(): OptionType? = when (this.javaType) {
    String::class.java -> OptionType.STRING
    Int::class.java -> OptionType.INTEGER
    Boolean::class.java -> OptionType.BOOLEAN
    Double::class.java -> OptionType.NUMBER
    User::class.java -> OptionType.USER
    Channel::class.java -> OptionType.CHANNEL
    Role::class.java -> OptionType.ROLE
    IMentionable::class.java -> OptionType.MENTIONABLE
    Message.Attachment::class.java -> OptionType.ATTACHMENT
    else -> null
}
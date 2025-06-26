package org.beespoon.artemis.util.extensions

import net.dv8tion.jda.api.interactions.commands.OptionMapping
import net.dv8tion.jda.api.interactions.commands.OptionType
import org.beespoon.artemis.util.extensions.optionType
import kotlin.reflect.KType

/**
 * Retrieves the option value based on the parameter type.
 *
 * This function returns the option value according to the parameter type. It uses the option type
 * determined by the parameter type to extract the appropriate value from the OptionMapping.
 *
 * @param parameterType The Kotlin KType representing the parameter type.
 * @return The option value corresponding to the parameter type, or null if no match is found.
 * @see OptionType
 * @see kotlin.reflect.KType
 * @see OptionMapping
 * @author <a href="https://www.bspoones.com">BSpoones</a>
 */
internal fun OptionMapping.getOptionValue(parameterType: KType): Any? = when (parameterType.optionType()) {
    OptionType.STRING -> asString
    OptionType.INTEGER -> asInt
    OptionType.BOOLEAN -> asBoolean
    OptionType.NUMBER -> asDouble
    OptionType.USER -> asUser
    OptionType.CHANNEL -> asChannel
    OptionType.ROLE -> asRole
    OptionType.MENTIONABLE -> asMentionable
    OptionType.ATTACHMENT -> asAttachment
    else -> null
}
package org.beespoon.artemis.util.extensions

import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import net.dv8tion.jda.internal.interactions.component.ButtonImpl

/**
 * Converts a ButtonStyle enum constant to a ButtonImpl instance.
 *
 * @receiver The ButtonStyle enum constant to convert.
 * @param customId The custom ID of the button.
 * @param label The label text displayed on the button.
 * @param disabled Whether the button is disabled. Default is false.
 * @param emoji The emoji displayed on the button. Default is null.
 * @return A ButtonImpl instance configured with the provided parameters.
 * @author <a href="https://www.bspoones.com">BSpoones</a>
 */
fun ButtonStyle.toButton(
    customId: String,
    label: String,
    disabled: Boolean = false,
    emoji: Emoji? = null
): ButtonImpl {
    return ButtonImpl(customId, label, this, disabled, emoji)
}
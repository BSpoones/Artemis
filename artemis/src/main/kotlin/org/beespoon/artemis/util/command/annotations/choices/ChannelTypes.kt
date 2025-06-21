package org.beespoon.artemis.util.command.annotations.choices

import net.dv8tion.jda.api.entities.channel.ChannelType

/**
 * Register an array of [ChannelType].
 *
 * This annotation will automatically register the command choices set here.
 *
 * Example usage:
 * ```kotlin
 * fun myCommand(
 *     event: // Command event
 *
 *     @CommandOption
 *     @ChannelTypes([ChannelType.TEXT, ChannelType.VOICE])
 *     channel: Channel
 * ) {}
 * ```
 * @property channelTypes Array of [ChannelType].
 * @author <a href="https://www.bspoones.com">BSpoones</a>
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class ChannelTypes(
    val channelTypes: Array<ChannelType>
)

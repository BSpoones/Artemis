package org.beespoon.artemis.command.annotation.choice.static

import net.dv8tion.jda.api.entities.channel.ChannelType

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class ChannelTypeChoices(vararg val choices: ChannelType)

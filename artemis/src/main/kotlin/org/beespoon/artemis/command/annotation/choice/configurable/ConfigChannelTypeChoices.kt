package org.beespoon.artemis.command.annotation.choice.configurable

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class ConfigChannelTypeChoices(val id: String)

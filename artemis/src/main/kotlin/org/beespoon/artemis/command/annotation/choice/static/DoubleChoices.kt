package org.beespoon.artemis.command.annotation.choice.static

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class DoubleChoices(vararg val choices: Double)
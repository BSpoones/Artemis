package org.beespoon.artemis.command.annotation.choice.static

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class IntegerChoices(vararg val choices: Int)
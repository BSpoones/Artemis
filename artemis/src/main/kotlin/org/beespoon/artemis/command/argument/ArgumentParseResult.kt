package org.beespoon.artemis.command.argument

class ArgumentParseResult<T> {

    fun pass(value: T) = value

    fun fail(reason: String) {

    }
}
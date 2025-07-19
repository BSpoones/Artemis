package org.beespoon.artemis.command.argument

sealed class ArgumentParseResult<out A> {
    data class Pass<out A>(val value: A) : ArgumentParseResult<A>()
    data class Fail(val error: String) : ArgumentParseResult<Nothing>()

    fun isPass(): Boolean = this is Pass

    fun getOrNull(): A? = (this as? Pass)?.value

    companion object {
        fun <A> success(value: A): ArgumentParseResult<A> = Pass(value)
        fun failure(error: String): ArgumentParseResult<Nothing> = Fail(error)
    }
}
package org.beespoon.artemis.command.argument

object ArgumentParseRegistry {

    private val parsers = mutableMapOf<KClass<*>, ArgumentParser<*, *>>()

    inline fun <reified Input : Any, reified Result : Any> register(parser: ArgumentParser<Input, Result>) {
        register(Input::class, parser)
    }

    fun <Input : Any, Result : Any> register(clazz: KClass<Input>, parser: ArgumentParser<Input, Result>) {
        if (parsers.containsKey(clazz)) {
            throw IllegalArgumentException("Parser for ${clazz.simpleName} is already registered.")
        }
        parsers[clazz] = parser
    }

    @Suppress("UNCHECKED_CAST")
    fun <Input : Any, Result : Any> parse(
        inputClass: KClass<Input>,
        event: GenericCommandInteractionEvent
    ): ArgumentParseResult<Result>? {
        val parser = parsers[inputClass] as? ArgumentParser<Input, Result> ?: return null
        return parser.parse(event)
    }

    inline fun <reified Input : Any, reified Result : Any> parse(event: GenericCommandInteractionEvent): ArgumentParseResult<Result>? {
        return parse(Input::class, event)
    }
}
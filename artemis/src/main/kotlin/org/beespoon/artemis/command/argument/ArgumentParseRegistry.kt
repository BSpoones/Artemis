package org.beespoon.artemis.command.argument

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass

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

    @Suppress("UNCHECKED_CAST")
    fun <Result : Any> suggestions(
        clazz: KClass<Result>,
        event: CommandAutoCompleteInteractionEvent
    ): CompletableFuture<List<String>> {
        val current = event.focusedOption.value
        val parser = parsers[clazz] as? ArgumentParser<String, Result>
            ?: return CompletableFuture.completedFuture(emptyList())
        return parser.suggestions(event, current)
    }

    inline fun <reified Result : Any> suggestions(event: CommandAutoCompleteInteractionEvent): CompletableFuture<List<String>> {
        return suggestions(Result::class, event)
    }
}
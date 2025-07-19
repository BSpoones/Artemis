package org.beespoon.artemis.command.argument

import net.dv8tion.jda.api.events.interaction.GenericAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent
import java.util.concurrent.CompletableFuture

abstract class ArgumentParser<Input, Result> {

    abstract fun parse(event: GenericCommandInteractionEvent): ArgumentParseResult<Result>

    open fun suggestions(event: GenericAutoCompleteInteractionEvent, current: Input): CompletableFuture<List<String>> {
        return CompletableFuture.completedFuture(emptyList())
    }
}
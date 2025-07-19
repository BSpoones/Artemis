package org.beespoon.artemis.command.argument

import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent
import java.util.concurrent.CompletableFuture

abstract class ArgumentParser<T> {

    abstract class parse<T>(event: GenericCommandInteractionEvent)

    open fun suggestions(event: GenericCommandInteractionEvent, current: String): CompletableFuture<List<String>> {
        return CompletableFuture.completedFuture(emptyList())
    }
}
package org.beespoon.artemis.command.option

import net.dv8tion.jda.api.entities.channel.Channel
import net.dv8tion.jda.api.interactions.commands.Command
import org.beespoon.artemis.command.annotation.choice.configurable.ConfigChannelTypeChoices
import org.beespoon.artemis.command.annotation.choice.configurable.ConfigDoubleChoices
import org.beespoon.artemis.command.annotation.choice.configurable.ConfigIntegerChoices
import org.beespoon.artemis.command.annotation.choice.configurable.ConfigStringChoices
import org.beespoon.artemis.command.annotation.choice.static.ChannelTypeChoices
import org.beespoon.artemis.command.annotation.choice.static.DoubleChoices
import org.beespoon.artemis.command.annotation.choice.static.IntegerChoices
import org.beespoon.artemis.command.annotation.choice.static.StringChoices
import org.beespoon.artemis.command.registry.CommandRegistry
import kotlin.reflect.KClass
import kotlin.reflect.KParameter

/**
 * Registers command options for all parameters with @Choice annotations
 *
 * @see org.bspoones.zeus.command.annotations.choices
 * @author <a href="https://www.bspoones.com">BSpoones</a>
 */
internal object ChoiceHandler {

    /**
     * All possible choices a command choice can be
     * mapped to the corresponding type
     */
    private val CHOICES = mapOf(
        StringChoices::class to String::class,
        IntegerChoices::class to Int::class,
        DoubleChoices::class to Double::class,
        ChannelTypeChoices::class to Channel::class,
        ConfigStringChoices::class to String::class,
        ConfigIntegerChoices::class to Int::class,
        ConfigDoubleChoices::class to Double::class,
        ConfigChannelTypeChoices::class to Channel::class,

    )

    /**
     * Builds a command choice list for slash command registration
     *
     * @param parameter [KParameter] - Method parameter
     * @return List<[Command.Choice]> - List of command choices
     * @author <a href="https://www.bspoones.com">BSpoones</a>
     */
    fun buildChoices(parameter: KParameter): List<Command.Choice> {
        return getChoices(parameter).mapNotNull { choice ->
            when (choice) {
                is String -> Command.Choice(choice, choice)
                is Double -> Command.Choice(choice.toString(), choice)
                is Long -> Command.Choice(choice.toString(), choice)
                else -> null
            }
        }
    }

    /**
     * Handles regular choices and variable choices
     *
     * @param parameter: [KParameter] - Method parameter
     * @return List<[Any]> - List of data type
     * @author <a href="https://www.bspoones.com">BSpoones</a>
     */
    fun getChoices(parameter: KParameter): List<Any> {
        val choiceAnnotations = parameter.annotations.filter { annotation ->
            (CHOICES.keys.map { it.simpleName }).contains(annotation.annotationClass.simpleName)
        }

        if (choiceAnnotations.isEmpty()) return listOf() // Command has no choices
        if (choiceAnnotations.size > 1) throw IllegalArgumentException("You can only have one choice type")

        if (parameter.type.classifier as? KClass<*> != (CHOICES)[choiceAnnotations.first().annotationClass])
            throw IllegalArgumentException("A parameter choice must be of the same type as the parameter")

        return when (val annotation = choiceAnnotations.first()) {
            is StringChoices -> annotation.choices.toList()
            is IntegerChoices -> annotation.choices.toList()
            is DoubleChoices -> annotation.choices.toList()
            is ChannelTypeChoices -> annotation.choices.toList()
            is ConfigStringChoices -> getVariableChoices(annotation)
            is ConfigIntegerChoices ->getVariableChoices(annotation)
            is ConfigDoubleChoices ->getVariableChoices(annotation)
            is ConfigChannelTypeChoices ->getVariableChoices(annotation)
            else -> throw IllegalArgumentException("Invalid Choice annotation")
        }
    }

    /**
     * Handles variable choice annotations by searching for its corresponding
     * unit in the customChoiceMap
     *
     * **NOTE: ALL VARIABLE CHOICES UNITS MUST BE DEFINED BEFORE COMMAND REGISTRATION**
     *
     * @param annotation [Annotation] - Choice annotation
     * @return List<[Any]> - Result from registered unit
     * @author <a href="https://www.bspoones.com">BSpoones</a>
     */
    private fun getVariableChoices(annotation: Annotation): List<Any> {
        val id = when(annotation) {
            is ConfigStringChoices -> annotation.id
            is ConfigIntegerChoices -> annotation.id
            is ConfigDoubleChoices -> annotation.id
            is ConfigChannelTypeChoices -> annotation.id
            else -> throw IllegalArgumentException("Invalid annotation type")
        }

        val result = CommandRegistry.getVariableChoice(id).invoke()

        return when {
            result is List<*> -> result.toList()
            else -> throw IllegalArgumentException("Variable method must return a List")
        }
    }

}
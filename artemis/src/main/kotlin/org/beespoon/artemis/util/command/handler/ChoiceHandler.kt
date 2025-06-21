package org.beespoon.artemis.util.command.handler

import net.dv8tion.jda.api.entities.channel.Channel
import net.dv8tion.jda.api.interactions.commands.Command
import org.beespoon.artemis.util.command.CommandRegistry
import org.beespoon.artemis.util.command.annotations.choices.*
import org.beespoon.artemis.util.command.annotations.choices.variable.*
import kotlin.reflect.KClass
import kotlin.reflect.KParameter

/**
 * Registers command options for all parameters with @Choice annotations
 *
 * @see org.beespoon.artemis.util.command.annotations.choices
 * @author <a href="https://www.bspoones.com">BSpoones</a>
 */
internal object ChoiceHandler {

    /**
     * All possible choices a command choice can be
     * mapped to the corresponding type
     */
    private val CHOICES = mapOf(
        StringChoices::class to String::class,
        LongChoices::class to Int::class,
        DoubleChoices::class to Float::class,
        ChannelTypes::class to Channel::class,
        VariableStringChoices::class to String::class,
        VariableLongChoices::class to Int::class,
        VariableDoubleChoices::class to Float::class,
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
            is DoubleChoices -> annotation.choices.toList()
            is LongChoices -> annotation.choices.toList()
            is VariableStringChoices -> getVariableChoices(annotation)
            is VariableDoubleChoices ->getVariableChoices(annotation)
            is VariableLongChoices ->getVariableChoices(annotation)
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
            is VariableStringChoices -> annotation.id
            is VariableDoubleChoices -> annotation.id
            is VariableLongChoices -> annotation.id
            else -> throw IllegalArgumentException("Invalid annotation type")
        }

        val result = CommandRegistry.getVariableChoice(id).invoke()

        return when {
            result is List<*> -> result.toList()
            else -> throw IllegalArgumentException("Variable method must return a List")
        }
    }

}
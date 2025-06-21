package org.beespoon.artemis.util.command.annotations.choices.variable

/**
 * Register a list of strings via a Unit result.
 *
 * Sets an option ID for the parameter, which is set to an option mapping via a
 * lambda. This is used for any slash command choices that change after build.
 * Each ID must be set via one of the methods below **before** registering the command.
 *
 * **IMPORTANT: YOU MUST SET YOUR VARIABLE CHOICE UNIT __BEFORE__ YOU REGISTER THE COMMAND!**
 * ```kotlin
 * // Variable method
 * val id: String = "CUSTOM ID HERE"
 * var functionality = {
 *     listof("One", "Two", "Three") // Logic here
 * }
 * CommandRegistry.serVariableChoice(id, functionality)
 *
 * // Command method
 * CommandRegistry.setVariableChoice("CUSTOM ID HERE") {
 *     listof("One", "Two", "Three") // Logic here
 * }
 * ```
 *
 * @property id Identifier for the choices.
 * @author <a href="https://www.bspoones.com">BSpoones</a>
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class VariableStringChoices(
    val id: String,
)
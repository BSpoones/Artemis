package org.beespoon.artemis.util.command.tree

import org.beespoon.artemis.util.command.enums.CommandType
import kotlin.reflect.KFunction

/**
 * **Command forest**
 *
 * Data structure to store command data in the form of leaves on a tree
 *
 * @author <a href="https://www.bspoones.com">BSpoones</a>
 */
internal object CommandForest {

    /**
     * Command leaves the central storage of registered commands
     * @author <a href="https://www.bspoones.com">BSpoones</a>
     */
    private val leaves: MutableList<CommandLeaf> = mutableListOf()

    /**
     * Retrieve the method (Command) from the command type and name
     *
     * @param type [CommandType] - Command Type
     * @param name [String] - Command name
     * @return [KFunction] - Command method, if any
     * @see CommandType
     * @author <a href="https://www.bspoones.com">BSpoones</a>
     */
    fun getFunction(type: CommandType, name: String): KFunction<*>? {
        return getLeaf(type, name)?.function
    }

    /**
     * Retrieve a command leaf
     * @param type [CommandType] - Command Type
     * @param name [String] - Command name
     * @return [CommandLeaf] - Command leaf
     * @see CommandType
     * @see CommandLeaf
     * @author <a href="https://www.bspoones.com">BSpoones</a>
     */
    private fun getLeaf(type: CommandType, name: String): CommandLeaf? {
        return getLeaves(type).find { it.name == name }
    }

    /**
     * Retrieve all leaves of a command type
     *
     * @param type [CommandType] - Command Type
     * @return List<[CommandLeaf]> - Command method, if any
     * @see CommandType
     * @see CommandLeaf
     * @author <a href="https://www.bspoones.com">BSpoones</a>
     */
    private fun getLeaves(type: CommandType): List<CommandLeaf> {
        return leaves.filter {
            it.type == type
        }
    }

    /**
     * Add a leaf to the command tree
     *
     * @param type [CommandType] - Command type
     * @param name [String] - Command name
     * @param function [KFunction] - Command method
     *
     * @see CommandType
     * @see org.beespoon.artemis.util.command.Command
     */
    fun addLeaf(type: CommandType, name: String, function: KFunction<*>) {
        // Name presence check
        if (leaves.filter { it.type == type }.map { it.name }.contains(name)) {
            throw IllegalArgumentException("Command name already registered: $name")
        }
        // Function presence check
        if (leaves.filter { it.type == type }.map { it.function }.contains(function)) {
            throw IllegalArgumentException("Function has already been registered elsewhere as a ${type.name}_COMMAND")
        }

        leaves.add(
            CommandLeaf(type, name, function)
        )
    }
}
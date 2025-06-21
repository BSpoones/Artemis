package org.beespoon.artemis.util.command.tree

import org.beespoon.artemis.util.command.enums.CommandType
import kotlin.reflect.KFunction

/**
 * Command Leaf
 *
 * Storage node for [CommandForest]
 */
internal class CommandLeaf(
    val type: CommandType,
    val name: String,
    val function: KFunction<*>
)
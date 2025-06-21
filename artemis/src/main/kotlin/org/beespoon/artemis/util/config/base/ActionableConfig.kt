package org.beespoon.artemis.util.config.base

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import java.nio.file.WatchEvent

/**
 * Abstract class to add additional features to a [ConfigFile]
 *
 * Example:
 *
 * ```kt
 *
 * @ConfigSerializable
 * class ExampleConfig: ActionableConfig() {
 *     var example: String = "Example config"
 *
 *     override fun onChange(event: WatchEvent<*>) {
 *         // Behaviour when a config file is changed should go here
 *     }
 * }
 * ```
 * @see org.beespoon.artemis.util.config.ConfigFile
 * @author <a href="https://www.bspoones.com">BSpoones</a>
 */
@ConfigSerializable
abstract class ActionableConfig: ArtemisConfig {

    /**
     * Behaviour to be run when a config file is changed
     *
     * @param event [WatchEvent] - Event context
     */
    abstract fun onChange(event: WatchEvent<*>)
}
package org.beespoon.artemis.util.config.anotation

/**
 * Specifies a config directory
 *
 * Example usage:
 *
 * ```kt
 * @ConfigSerializable
 * @ConfigDirectory("example-directory")
 * class ExampleConfig {
 *     var text: String = "This is example text"
 * }
 * ```
 * @property directory [String]: Directory for the config file to go in
 * @see org.beespoon.artemis.util.config.ConfigFile
 * @author <a href="https://www.bspoones.com">BSpoones</a>
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class ConfigDirectory(val directory: String = "")
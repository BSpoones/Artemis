package org.beespoon.artemis.util.config

import org.beespoon.artemis.Artemis
import org.beespoon.artemis.util.config.anotation.ConfigDirectory
import org.beespoon.artemis.util.config.base.ArtemisConfig
import org.beespoon.artemis.util.config.base.CommandConfig
import org.beespoon.artemis.util.command.CommandRegistry

inline fun <reified T : ArtemisConfig> getConfig(): T = ConfigManager.getConfig(T::class.java)
fun <T : ArtemisConfig> getConfig(config: Class<T>): T = ConfigManager.getConfig(config)
fun initConfig(vararg clazzes: Class<out ArtemisConfig>) =
    clazzes.forEach { ConfigManager.initConfig(it) }

// TODO -> JavaDoc
object ConfigManager : AutoCloseable {
    private val configMap: MutableMap<Class<out ArtemisConfig>, ConfigFile<out ArtemisConfig>> = mutableMapOf()

    override fun close() {
        configMap.values.forEach {
            Artemis.logger.debug("Closing ${it.clazz.simpleName}.json")
            it.close()
        }
    }

    fun initConfig(config: Class<out ArtemisConfig>) {
        Artemis.logger.debug("Initialising ${config.simpleName}")
        val directory = config.getAnnotationsByType(ConfigDirectory::class.java).firstOrNull()?.directory ?: ""

        try {
            configMap[config] = ConfigFile(config, directory)
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        registerCommandConfig(config)

    }

    @Suppress("UNCHECKED_CAST")
    fun <T: ArtemisConfig> getConfig(config: Class<T>): T {
        Artemis.logger.debug("Fetching config file of ${config.simpleName}")
        return configMap[config]?.getConfig() as? T
            ?: throw IllegalStateException("The class ${config.simpleName} has not been initialised!")
    }

    private fun <T> registerCommandConfig(config: Class<T>) {
        val configFile = getConfig(config) ?: return
        if (configFile is CommandConfig) {
            CommandRegistry.registerCommandConfig(configFile)
        }
    }
}
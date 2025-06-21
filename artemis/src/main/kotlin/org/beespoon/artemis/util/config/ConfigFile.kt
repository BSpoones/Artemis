package org.beespoon.artemis.util.config

import org.beespoon.artemis.Artemis
import org.beespoon.artemis.util.config.base.ActionableConfig
import org.beespoon.artemis.util.config.base.ArtemisConfig
import org.spongepowered.configurate.BasicConfigurationNode
import org.spongepowered.configurate.gson.GsonConfigurationLoader
import org.spongepowered.configurate.reference.ConfigurationReference
import org.spongepowered.configurate.reference.ValueReference
import org.spongepowered.configurate.reference.WatchServiceListener
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchEvent

private const val PATH_NAME = "config"

/**
 * Configuration file builder.
 *
 * This will convert a serialised class to a json file in [PATH_NAME] directory
 *
 * Config files can be placed in the root config directory ([PATH_NAME])
 *
 * ```kt
 * @ConfigSerializable
 * class ExampleConfig: ArtemisConfig() {
 *     var text: String = "This is example text"
 * }
 * ```
 *
 * It is also possible for configurations to be made to a config file, for example, a nested
 * directory can be set via the `directory` parameter in ArtemisConfig
 *
 * @author <a href="https://www.bspoones.com">BSpoones</a>
 * @param clazz [Any] The Class that should be turned into a config file
 * @param directory [String] The additional directory to place the config file in
 * @see org.spongepowered.configurate.objectmapping.ConfigSerializable
 * @see org.beespoon.artemis.util.config.base.ActionableConfig
 */
internal class ConfigFile<T: ArtemisConfig>(val clazz: Class<T>, private val directory: String = "") : AutoCloseable {

    /**
     * Directory creation, will nest the file directory if [directory] is given
     */
    private val realDirectory = directory.trim().removeSuffix("/") + File.separator
    private val directoryPath = Paths.get(PATH_NAME + File.separator + realDirectory)
    private val filePath: Path = directoryPath.resolve("${clazz.simpleName}.json")

    private lateinit var ref: ConfigurationReference<BasicConfigurationNode>
    private lateinit var configFile: ValueReference<T, BasicConfigurationNode>

    private val watchListener: WatchServiceListener = WatchServiceListener.create()

    private var isSetup: Boolean = false
    private var lastEdit: Long = 0

    init {
        setup()
    }

    /**
     * Sets up the config file, this is run on boot and does not overwrite existing data
     * If existing data is not found, a config file with default data is created
     */
    private fun setup() {
        isSetup = false
        try {
            createFile()
            setupListener()
            Artemis.logger.debug("Finished config setup for ${clazz.simpleName}")
            isSetup = true
        } catch (t: Throwable) {
            Artemis.logger.error("Failed to create config file for ${clazz.simpleName} - $t")
        }
    }

    /**
     * Will create the target directory or directories if not present and will also create an empty
     * json file to be edited
     */
    private fun createFile() {
        Artemis.logger.debug("Checking for directory path and file path")
        if (Files.notExists(directoryPath)) {
            Artemis.logger.debug("Directory path does not exist, creating path")
            Files.createDirectories(directoryPath)
        }
        if (Files.notExists(filePath)) {
            Artemis.logger.debug("File path does not exist, creating path")
            Files.createFile(filePath)
        }
        Artemis.logger.debug("Directory and file paths have been created!")
    }

    /**
     * Loader initialisation, creates the Gson file loader to convert a config
     * class to a json file
     */
    private fun createLoader(path: Path): GsonConfigurationLoader {
        Artemis.logger.debug("Creating GsonConfigurationLoader")
        return GsonConfigurationLoader.builder()
            .defaultOptions {
                it.shouldCopyDefaults(true)
            }
            .path(path)
            .lenient(true)
            .build()
    }

    /**
     * Initialises the file watcher, setting up actions when a file is
     * altered
     */
    private fun setupListener() {
        Artemis.logger.debug("Creating WatchListener listener")
        ref = watchListener.listenToConfiguration(
            { createLoader(it) },
            filePath
        )
        Artemis.logger.debug("Creating config file reference")

        configFile = ref.referenceTo(clazz)

        watchListener.listenToFile(filePath) { event ->
            if (!isSetup) return@listenToFile

            /**
             * If the config file has been deleted while a bot is still online,
             * assume this is an attempt to reset the config file to default settings
             */
            if (Files.notExists(filePath)) {
                Artemis.logger.info("${clazz.simpleName} has been deleted, resetting to default!")
                setup()
                return@listenToFile
            }

            if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                /**
                 * Executes any custom changes
                 */
                if (isActionableConfig()) {
                    if (System.currentTimeMillis() - lastEdit > 1_000) {
                        Artemis.logger.info("Updating ${filePath.fileName}")
                        (getConfig() as ActionableConfig).onChange(event)
                    }
                    lastEdit = System.currentTimeMillis()
                }
            }
        }
        Artemis.logger.debug("Saving ConfigurationReference")
        ref.save()
    }

    private fun isActionableConfig(): Boolean = ActionableConfig::class.java.isAssignableFrom(clazz)

    @Suppress("UNCHECKED_CAST")
    private fun getAutoConfig(): Class<out ActionableConfig> = clazz as Class<out ActionableConfig>

    /**
     * Retrieve the latest config file cache
     */
    fun getConfig(): T? = this.configFile.get()

    override fun close() {
        Artemis.logger.debug("Closing ${filePath.fileName}")
        ref.save()
        ref.close()
        watchListener.close()
    }
}
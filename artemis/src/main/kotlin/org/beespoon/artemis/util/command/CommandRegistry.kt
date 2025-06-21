package org.beespoon.artemis.util.command

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.exceptions.ContextException
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import org.bspoones.zeus.config.files.ZeusConfig
import org.bspoones.zeus.config.getConfig
import org.beespoon.artemis.util.command.handler.CommandTreeHandler
import org.beespoon.artemis.util.command.config.CommandConfig
import org.beespoon.artemis.util.command.config.SerialisedCommand
import org.bspoones.zeus.logging.ZeusLogger
import org.bspoones.zeus.logging.getZeusLogger
import org.bspoones.zeus.util.scheduling.async
import java.time.Duration
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

/**
 * **Command Registry**
 *
 * Responsible for all group, command, option, choice registration
 *
 * @author <a href="https://www.bspoones.com">BSpoones</a>
 */
internal object CommandRegistry {
    private val logger: ZeusLogger = getZeusLogger("Command Handler")

    lateinit var api: JDA

    val autoCompleteMap: MutableMap<String, Map<String, List<Any>>> = mutableMapOf()
    private val customChoiceMap: MutableMap<String, () -> Collection<Any>> = mutableMapOf()
    private var commandRegistry: MutableList<CommandData> = mutableListOf()

    private var commandConfigs: MutableList<CommandConfig> = mutableListOf()


    /**
     * Register all commands within a class Object
     *
     * ```kotlin
     * object ExampleCommand: Command() {
     *
     *     @SlashCommand("name","description")
     *     fun onNameCommand(...) {...}
     * }
     *
     * CommandRegistry.registerCommands(
     *     ExampleCommand::class,
     *     AnotherCommand::class,
     *     ThirdCommand::class,
     *     guildOnly = true // Optional
     * )
     * ```
     *
     * @param commands [KClass] - Command Objects (see above)
     * @param guildOnly [Boolean] - Register all commands to a list of guilds. Default = false
     * @see org.beespoon.artemis.util.command.Command
     * @author <a href="https://www.bspoones.com">BSpoones</a>
     */
    fun registerCommands(vararg commands: KClass<*>, guildOnly: Boolean = false) {
        var commandCount: Int = 0
        commandRegistry = mutableListOf()
        logger.info("Registering ${commands.size} ${if (guildOnly) "guild " else ""}command objects")
        commands.forEach { clazz ->
            CommandTreeHandler.buildCommandTree(clazz).forEach { command ->
                val commandGuildOnly = command.isGuildOnly || guildOnly
                commandRegistry.add(command.setGuildOnly(commandGuildOnly))
                logger.debug("${command.name} registered as a ${if (commandGuildOnly) "guild" else "global"} command.")
                commandCount += 1
            }
        }
        logger.info("$commandCount commands registered!")

        if (!guildOnly) {
            registerGlobalCommands()
        }
        registerGuildCommands()
    }

    fun registerCommandConfig(config: CommandConfig) {
        // Done async as thread locking operations are required
        async {
            val latestConfig = getConfig(config::class.java) // Ensures latest version is fetched on registration

            val cachedGuilds: MutableMap<Long, Guild> = mutableMapOf()
            val commandData = latestConfig.commandData()
            logger.debug("Registering Command Config: ${latestConfig::class.simpleName}")

            removeExistingCommandConfig(config)
            latestConfig.guildIds.forEach { guildId ->
                val guild = cachedGuilds[guildId] ?: api.getGuildById(guildId) ?: return@forEach
                cachedGuilds[guildId] = guild
                commandData.forEach {
                    guild.upsertCommand(it).queueAfter(5, TimeUnit.SECONDS) {
                        logger.info("Successfully re-registered ${it.type} - ${it.name} in ${guild.name}")
                    }
                }
            }
            commandConfigs.add(latestConfig)
        }
    }

    private fun removeExistingCommandConfig(config: CommandConfig) {
        val existing = commandConfigs.find { it::class.simpleName == config::class.simpleName } ?: return
        val cachedGuilds: MutableMap<Long, Guild> = mutableMapOf()

        val deletedCommands = existing.commands.map { Pair(it.commandTypes, it.name) }
            .toSet() - config.commands.map { Pair(it.commandTypes, it.name) }.toSet()

        existing.guildIds.forEach { guildId ->
            val guild = cachedGuilds[guildId] ?: api.getGuildById(guildId) ?: return@forEach
            cachedGuilds[guildId] = guild

            guild.retrieveCommands().queue { commands ->
                deletedCommands.forEach { commandData ->
                    // Complete required in order to ensure command is successfully deleted
                    commandData.first.forEach { commandType ->
                        commands.find { it.name == commandData.second && it.type == commandType.toDiscord() }?.delete()
                            ?.queue()
                    }

                }
            }
        }
        commandConfigs.removeAll { it::class.simpleName == config::class.simpleName }
    }


    fun findConfigCommand(guildId: Long, name: String): SerialisedCommand? {
        return commandConfigs.find {
            it.guildIds.contains(guildId) && it.commands.map { it.name }.contains(name)
        }
            ?.commands?.find { it.name == name }
    }

    /**
     * Set variable choice unit
     *
     * The provided method will be called to get a list of command choices whenever
     * the commands are re-registered
     *
     * @param id [String] - Custom choice ID. This will be checked against the Method (Command) option ID
     * @param block [Unit] - Unit that returns a list of the required type
     * @see org.beespoon.artemis.util.command.annotations.choices.variable
     * @see org.beespoon.artemis.util.command.handler.OptionHandler.buildOptions
     * @author <a href="https://www.bspoones.com">BSpoones</a>
     */
    fun setVariableChoice(id: String, block: () -> Collection<Any>) {
        customChoiceMap[id] = block
    }

    /**
     * Retrieve the variable choice unit form the choice map
     *
     * @param id [String] - Custom choice ID. This should be set **before the commands are registered**
     *
     * @return Collection<[Any]> - Collection of choices
     * @see org.beespoon.artemis.util.command.annotations.choices.variable
     * @see org.beespoon.artemis.util.command.handler.OptionHandler.buildOptions
     * @author <a href="https://www.bspoones.com">BSpoones</a>
     */
    fun getVariableChoice(id: String): () -> Collection<Any> {
        return customChoiceMap[id] ?: throw NoSuchMethodException("Unable to find variable declaration for $id")
    }


    /**
     * Register all global commands to the API
     *
     * @author <a href="https://www.bspoones.com">BSpoones</a>
     */
    private fun registerGlobalCommands() {
        api.updateCommands().addCommands(commandRegistry.filter { !it.isGuildOnly }).queue()
    }

    /**
     * Register all global commands to the all guilds set in [guilds]
     *
     * @author <a href="https://www.bspoones.com">BSpoones</a>
     */
    private fun registerGuildCommands() {
        getConfig<ZeusConfig>().whitelistedGuildIds.forEach { guildID ->
            val guild = api.getGuildById(guildID) ?: return@forEach
            guild.updateCommands().addCommands(commandRegistry.filter { it.isGuildOnly }).complete()
        }
    }


    /**
     * Retrieves the guild prefix set in [prefixGuildMap], if any
     *
     * @author <a href="https://www.bspoones.com">BSpoones</a>
     */
    fun getPrefix(guildId: Long): String {
        val config = getConfig<ZeusConfig>()
        return config.guildPrefixMap[guildId] ?: config.globalMessagePrefix
    }

    /**
     * Set up the Command Registry
     *
     * This **should** be done within Zeus itself, so you don't have to worry!
     *
     * @param api [JDA] - Discord bot instance
     *
     * @see JDA
     * @see org.beespoon.artemis.util.command.annotations.GuildOnly
     * @see org.beespoon.artemis.util.command.handler.GuildOnlyHandler
     *
     * @author <a href="https://www.bspoones.com">BSpoones</a>
     */
    fun setup(
        api: JDA
    ) {
        this.api = api
    }

}



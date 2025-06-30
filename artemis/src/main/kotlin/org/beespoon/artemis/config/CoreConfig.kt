package org.beespoon.artemis.config

import net.dv8tion.jda.api.requests.GatewayIntent
import org.beespoon.artemis.Artemis
import org.beespoon.artemis.util.config.anotation.ConfigDirectory
import org.beespoon.artemis.util.config.base.ActionableConfig
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import java.nio.file.WatchEvent
import kotlin.system.exitProcess

/**
 * Main Artemis Config file
 *
 * This file contains all setup configuration
 */
@ConfigSerializable
@ConfigDirectory("artemis")
internal class CoreConfig : ActionableConfig() {
    var token: String = ""

    var globalMessagePrefix: String = "!"

    var whitelistedGuildIds: MutableList<Long> = mutableListOf()

    var guildPrefixMap: MutableMap<Long, String> = mutableMapOf()

    var gatewayIntents: MutableList<GatewayIntent> = GatewayIntent.entries.toMutableList()

    // Any edits to this file should require a restart
    override fun onChange(event: WatchEvent<*>) {
        Artemis.logger.warn("Core Config has been changed! Restarting in:")
        for (i in 5 downTo 1) {
            println(i)
            Thread.sleep(1_000)
        }
        exitProcess(1)
    }
}
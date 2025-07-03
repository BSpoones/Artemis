package org.beespoon.artemis.api

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import org.beespoon.artemis.config.CoreConfig
import org.beespoon.artemis.util.config.getConfig

object JdaApi {

    private lateinit var jda: JDA

    fun setup() {
        val config = getConfig<CoreConfig>()

        if (config.token.isBlank()) {
            throw IllegalStateException("Discord bot token is not set in the configuration.")
        }

        jda = JDABuilder.createDefault(
            config.token,
            config.gatewayIntents
        ).build()
        jda.awaitReady()
    }
}
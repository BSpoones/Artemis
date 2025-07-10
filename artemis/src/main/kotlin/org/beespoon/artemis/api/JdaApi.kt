package org.beespoon.artemis.api

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import org.beespoon.artemis.config.CoreConfig
import org.beespoon.artemis.util.config.getConfig

object JdaApi {

    private lateinit var jda: JDA

    fun api(): JDA {
        if (this::jda.isInitialized) {
            return jda
        } else {
            throw IllegalArgumentException("JDA Api has not been initialised!")
        }
    }

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
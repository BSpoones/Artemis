package org.beespoon.artemis.util.text


object LogConstants {

    fun logConfigErrorMessage() {
        println(SETUP_CONFIG_MESSAGE.joinToString("\n"))
    }

    fun logMongoErrorMessage() {
        println(MONGO_FAIL_MESSAGE.joinToString("\n"))
    }


    private val SETUP_CONFIG_MESSAGE = listOf(
        "                  +------------------------------+",
        "                  | Artemis setup is incomplete! |",
        "                  +------------------------------+",
        "\n",
        "Attention: Artemis Setup is incomplete",
        "\n",
        "Please fill in the generated config file in /config/artemis/CoreConfig.json",
        "For the bot to work properly, please enter your bot token.",
        "\n",
        "If you plan on testing your bot, or would only like your bot to work in",
        "certain servers, please enter your desired server IDs in guild-prefix-map",
        "\n",
        "Below you will find a value called gateway-intents, this contains all possible",
        "intents for a discord bot to have, please adjust as necessary"
    )

    private val MONGO_FAIL_MESSAGE = listOf(
        "                  +------------------------------+",
        "                  | Artemis MongoDB connection   |",
        "                  | failed! Please check your    |",
        "                  | configuration and try again. |",
        "                  +------------------------------+",
        "\n",
        "Attention: Artemis MongoDB connection failed!",
        "\n",
        "Please check your MongoDB configuration in /config/artemis/MongoConfig.json.",
        "Ensure that the connection string is correct and that the MongoDB server is running."
    )
}
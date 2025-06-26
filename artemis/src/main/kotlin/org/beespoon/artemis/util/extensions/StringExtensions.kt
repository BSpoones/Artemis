package org.beespoon.artemis.util.extensions

import java.awt.Color
import java.net.URLEncoder

val USER_PING_REGEX = "<@!?(\\d+)>".toRegex()
val ROLE_PING_REGEX = "<@&!?(\\d+)>".toRegex()
val CHANNEL_PING_REGEX = "<#!?(\\d+)>".toRegex()
val NUMBER_ONLY_REGEX = "\\d+".toRegex()

fun String.isUserPing(): Boolean = USER_PING_REGEX.matches(this)
fun String.isRolePing(): Boolean = ROLE_PING_REGEX.matches(this)
fun String.isChannelPing(): Boolean = CHANNEL_PING_REGEX.matches(this)
fun String.numbersOnly(): String? = NUMBER_ONLY_REGEX.find(this)?.value


fun String.toColor(): Color {
    val hex = this.removePrefix("#")
    return when (hex.length) {
        6 -> Color(
            hex.substring(0, 2).toInt(16),
            hex.substring(2, 4).toInt(16),
            hex.substring(4, 6).toInt(16)
        )
        8 -> Color(
            hex.substring(0, 2).toInt(16),
            hex.substring(2, 4).toInt(16),
            hex.substring(4, 6).toInt(16),
            hex.substring(6, 8).toInt(16)
        )
        else -> throw IllegalArgumentException("Invalid hex color format")
    }
}

fun String.URLEncode() = URLEncoder.encode(this, "UTF-8")
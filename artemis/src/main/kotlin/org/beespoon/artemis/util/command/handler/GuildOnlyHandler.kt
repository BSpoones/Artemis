package org.beespoon.artemis.util.command.handler

import org.beespoon.artemis.util.command.annotations.GuildOnly
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.hasAnnotation

/**
 * Determining if an object or method is guildOnly
 *
 * @see org.beespoon.artemis.util.command.annotations.GuildOnly
 * @author <a href="https://www.bspoones.com">BSpoones</a>
 */
internal object GuildOnlyHandler {
    fun buildGuildOnly(method: KFunction<*>) : Boolean{
        return method.hasAnnotation<GuildOnly>()
    }

    fun buildGuildOnly(clazz: KClass<*>) : Boolean{
        return clazz.hasAnnotation<GuildOnly>()
    }
}
package org.beespoon.artemis.bootstrapper

import org.beespoon.artemis.ArtemisModule
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.net.URLClassLoader
import java.util.*

class ArtemisModuleLoader(val loader: ClassLoader) {

    private val MODULE_DIRECTORY = "modules"

    private val logger: Logger = LoggerFactory.getLogger("Artemis-ModuleLoader")
    private val modules: MutableMap<String, ArtemisModule> = mutableMapOf()

    fun loadModules() {
        val startTime = System.nanoTime()
        logger.info("Loading modules")

        val jars = getModuleDirectory().listFiles { f -> f.extension == "jar" } ?: emptyArray()
        val urls = jars.map { it.toURI().toURL() }.toTypedArray()
        val loader = URLClassLoader(urls, loader)

        val serviceLoader = ServiceLoader.load(ArtemisModule::class.java, loader)
        serviceLoader.forEach { module ->
            if (modules.keys.contains(module.name)) {
                throw RuntimeException("Module (${module::class.java.name}) with name ${module.name} already exists")
            }

            logger.info("Loading Module ${module.name}")
            modules[module.name] = module
            logger.info("Module ${module.name} loaded")
        }

        modules.entries.forEach { (name, module) ->
            logger.info("Starting Module $name")
            module.onStart()
            logger.info("Module $name Started")

        }

        logger.info("${modules.size} modules loaded!")

        // TODO - Config loading, Command loading, etc.
    }

    fun unloadModules() {
        modules.values.forEach {
            try {
                it.onStop()
            } catch (e: Exception) {
                return@forEach
            }
        }
        modules.clear()
    }

    private fun getModuleDirectory(): File {
        return File(MODULE_DIRECTORY).apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }
}
package org.beespoon.artemis

import org.beespoon.artemis.bootstrapper.ArtemisModuleLoader
import org.slf4j.LoggerFactory

object Artemis {

    val logger = LoggerFactory.getLogger("Artemis-Core")

    fun startUp() {

        ArtemisModuleLoader(this.javaClass.classLoader).loadModules()

    }


}
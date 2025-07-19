package org.beespoon.artemis

abstract class ArtemisModule(val name: String){

    open fun onStart() {}
    open fun onStop() {}
}
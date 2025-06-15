plugins {
    kotlin("jvm") version "1.9.10"
}

allprojects {
    group = "org.beespoon.artemis"
}

repositories {
    mavenCentral()
}

java {
    withSourcesJar()
}
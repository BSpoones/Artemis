plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("artemisCoreConventions") {
            id = "artemis.core-conventions"
            implementationClass = "artemis.ArtemisCoreConventionsPlugin"
        }
    }
}
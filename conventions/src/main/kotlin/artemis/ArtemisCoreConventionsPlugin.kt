package artemis

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.repositories

class ArtemisCoreConventionsPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

        pluginManager.apply("java-library")
        pluginManager.apply("org.jetbrains.kotlin.jvm")

        repositories {
            mavenCentral()
        }


        dependencies {
            add("implementation", libs.findLibrary("jda").get())
            add("implementation", libs.findLibrary("mongodb").get())
            add("implementation", libs.findLibrary("morphia-core").get())
            add("implementation", libs.findLibrary("morphia-kotlin").get())
            add("implementation", libs.findLibrary("configurate").get())
            add("implementation", libs.findLibrary("guava").get())
            add("implementation", libs.findLibrary("slf4j").get())
        }
    }
}

rootProject.name = "Artemis"

includeBuild("conventions")

val PLUGINS = listOf(
    "artemis-core"
).map { Project(it) }

PLUGINS.forEach(Project::setup)

class Project(private val name: String) {
    fun setup() {
        include(name)
        project(":$name").apply {
            projectDir = file(name)
        }
    }
}

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.attributes.Attribute

class NoHideApiPlugin:Plugin<Project> {
    override fun apply(project: Project) {
        println("no hide api plugin run")
        val artifactType = Attribute.of("nohideapi", Boolean::class.javaObjectType)
        project.dependencies.attributesSchema.attribute(artifactType)
        project.dependencies
            .artifactTypes
            .asMap
            .forEach {
                it.value.attributes.attribute(artifactType, false)
            }
        project.afterEvaluate {
            project.configurations.forEach { files ->
                if (files.isCanBeResolved) {
                    files.attributes.attribute(artifactType, true)
                }
            }
        }
        project.dependencies
            .registerTransform(NoHideApiAction::class.java) {
                from.attribute(artifactType, false)
                to.attribute(artifactType, true)
            }
    }
}
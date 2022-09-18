import org.gradle.api.Plugin
import org.gradle.api.Project

class NoHideApiPlugin:Plugin<Project> {
    override fun apply(target: Project) {
       println("no hide api plugin run")
    }
}
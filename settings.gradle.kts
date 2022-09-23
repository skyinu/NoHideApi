pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
rootProject.name = "NoHideApi"
include(":app")
//https://discuss.gradle.org/t/how-can-i-publish-to-maven-the-plugin-defined-at-buildsrc/42039
include("plugin")

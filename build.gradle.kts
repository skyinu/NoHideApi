buildscript {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        mavenLocal()
    }
    //https://github.com/google/iosched/blob/main/build.gradle.kts
    dependencies {
        classpath("com.github.skyinu.NoHideApi:nohideapiplugin:0.1.1")
    }
}// Top-level build file where you can add configuration options common to all sub-projects/modules.
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        mavenLocal()
    }
}
plugins {
    id("com.android.application") version "7.2.2" apply false
    id("com.android.library") version "7.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.5.31" apply false
}
tasks.register("clean", Delete::class){
    delete(rootProject.buildDir)
}
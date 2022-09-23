/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    `kotlin-dsl`
    `maven-publish`
    `java-gradle-plugin`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation(gradleApi())
    implementation(gradleKotlinDsl())
    implementation("org.ow2.asm:asm:9.3")
    implementation("com.github.javaparser:javaparser-symbol-solver-core:3.24.2")
}
//https://docs.gradle.org/current/userguide/publishing_maven.html
publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.skyinu"
            artifactId = "nohideapiplugin"
            version = "0.1"
            from(components["java"])
        }
    }
}
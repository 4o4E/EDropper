import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "top.e404"
version = "1.0.0"
val epluginVersion = "1.0.5"

fun kotlinx(id: String, version: String) = "org.jetbrains.kotlinx:kotlinx-$id:$version"
fun eplugin(id: String, version: String = epluginVersion) = "top.e404:eplugin-$id:$version"

repositories {
    mavenLocal()
    // spigot
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/groups/public/")
    // placeholderAPI
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    // engine hub
    maven("https://maven.enginehub.org/repo/")
    // papi
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    mavenCentral()
}

dependencies {
    // spigot
    compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
    // eplugin
    implementation(eplugin("core"))
    implementation(eplugin("serialization"))
    implementation(eplugin("hook-worldedit"))
    implementation(eplugin("hook-placeholderapi"))
    // placeholderAPI
    compileOnly("me.clip:placeholderapi:2.11.1")
    // world edit
    compileOnly("com.sk89q.worldedit:worldedit-core:7.2.7")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.2.7")
    // serialization
    implementation(kotlinx("serialization-core-jvm", "1.3.3"))
    implementation(kotlinx("serialization-json", "1.3.3"))
}

tasks {
    withType<KotlinCompile>() {
        kotlinOptions.jvmTarget = "1.8"
    }

    processResources {
        filesMatching("plugin.yml") {
            expand(project.properties)
        }
    }

    shadowJar {
        archiveFileName.set("${project.name}-${project.version}.jar")
        exclude("META-INF/*")
        relocate("kotlin", "top.e404.edropper.relocate.kotlin")
        relocate("top.e404.eplugin", "top.e404.edropper.relocate.eplugin")

        doFirst {
            for (file in File("jar").listFiles() ?: arrayOf()) {
                println("正在删除`${file.name}`")
                file.delete()
            }
        }

        doLast {
            File("jar").mkdirs()
            for (file in File("build/libs").listFiles() ?: arrayOf()) {
                println("正在复制`${file.name}`")
                file.copyTo(File("jar/${file.name}"), true)
            }
        }
    }
}
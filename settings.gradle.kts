rootProject.name = "data-driven-composter"

pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/") {
            name = "Fabric"
        }
        maven("https://maven.quiltmc.org/repository/release/") {
            name = "Quilt"
            content {
                includeGroup("org.quiltmc")
            }
        }
        gradlePluginPortal()
    }
    plugins {
        id("fabric-loom") version System.getProperty("loomVersion")!!
        id("com.diffplug.spotless") version System.getProperty("spotlessVersion")!!
        id("com.modrinth.minotaur") version System.getProperty("minotaurVersion")!!
        id("io.github.juuxel.loom-quiltflower") version System.getProperty("quiltflowerVersion")!!
    }
}


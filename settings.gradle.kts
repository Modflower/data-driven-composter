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
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("libs.versions.toml"))
        }
    }
}

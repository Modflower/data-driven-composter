import java.net.URLEncoder
import java.nio.charset.StandardCharsets

plugins {
	java
	`java-library`
	alias(libs.plugins.loom)
	alias(libs.plugins.spotless)
	alias(libs.plugins.minotaur)
	`maven-publish`
}

val minecraftVersion = libs.versions.minecraft.version.get()
val minecraftCompatible = libs.versions.minecraft.compatible.get()
val fabricApiVersion = libs.versions.fabric.api.get()
val projectVersion: String by project
val modrinthId: String by project

val isPublish = System.getenv("GITHUB_EVENT_NAME") == "release"
val isRelease = System.getenv("BUILD_RELEASE").toBoolean()
val isActions = System.getenv("GITHUB_ACTIONS").toBoolean()
val baseVersion = "$projectVersion+mc.$minecraftVersion"

version = when {
	isRelease -> baseVersion
	isActions -> "$baseVersion-build.${System.getenv("GITHUB_RUN_NUMBER")}-commit.${System.getenv("GITHUB_SHA").substring(0, 7)}-branch.${System.getenv("GITHUB_REF")?.substring(11)?.replace('/', '.') ?: "unknown"}"
	else -> "$baseVersion-build.local"
}

java {
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {
	minecraft(libs.minecraft)
	mappings(variantOf(libs.yarn) { classifier("v2") })
	modImplementation(libs.bundles.fabric)
	include(modImplementation(fabricApi.module("fabric-lifecycle-events-v1", fabricApiVersion))!!)
	include(modImplementation(fabricApi.module("fabric-api-base", fabricApiVersion))!!)
	compileOnly(libs.bundles.compile)
}
spotless {
	java {
		importOrderFile(projectDir.resolve(".internal/spotless.importorder"))
		eclipse().configFile(projectDir.resolve(".internal/spotless.xml"))

		licenseHeaderFile(projectDir.resolve(".internal/license-header.java"))
	}
}
tasks {
	withType<JavaCompile> {
		options.encoding = "UTF-8"
		options.isDeprecation = true
		options.isWarnings = true
	}
	processResources {
		val map = mapOf(
			"version" to project.version,
			"project_version" to projectVersion,
			"minecraft_required" to libs.versions.minecraft.required.get()
		)
		inputs.properties(map)

		filesMatching("fabric.mod.json") {
			expand(map)
		}
	}
	withType<Jar> {
		from("LICENSE")
	}
	modrinth {
		token.set(System.getenv("MODRINTH_TOKEN"))
		projectId.set(modrinthId)
		versionType.set(
			System.getenv("RELEASE_OVERRIDE") ?: when {
				"alpha" in projectVersion -> "alpha"
				!isRelease || '-' in projectVersion -> "beta"
				else -> "release"
			}
		)
		val ref = System.getenv("GITHUB_REF")
		changelog.set(
			System.getenv("CHANGELOG") ?: if (ref != null && ref.startsWith("refs/tags/")) "You may view the changelog at https://github.com/Modflower/data-driven-composter/releases/tag/${URLEncoder.encode(ref.substring(10), StandardCharsets.UTF_8)}"
			else "No changelog is available. Perhaps poke at https://github.com/Modflower/data-driven-composter for a changelog?"
		)
		uploadFile.set(remapJar.get())
		gameVersions.set(minecraftCompatible.split(","))
		loaders.addAll("fabric", "quilt")
	}
}


import java.time.LocalDateTime.now
import java.time.format.DateTimeFormatter.ofPattern

val junitVersion = "6.1.2"

buildscript {
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
}

plugins {
  groovy
  `maven-publish`
  `java-gradle-plugin`
  id("org.jetbrains.kotlin.jvm") version "2.4.0"
  id("com.gradle.plugin-publish") version "2.1.1"
}

group = "de.europace.gradle"
version = now().format(ofPattern("yyyyMMddHHmmss"))
logger.lifecycle("version: $version")

val dependencyVersions = listOf(
    "org.junit:junit-bom:${junitVersion}"
)

val dependencyVersionsByGroup = mapOf(
    "org.apache.groovy" to "4.0.32",
    "org.jetbrains.kotlin" to "2.4.0",
    "org.junit.jupiter" to junitVersion,
    "org.junit.platform" to junitVersion
)

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
  compilerOptions {
    jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
  }
}

tasks.withType(Test::class) {
  useJUnitPlatform()
}

repositories {
  mavenCentral()
}

dependencies {
  implementation(gradleApi())
  testImplementation(localGroovy())
  testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
  testImplementation("org.spockframework:spock-core:2.4-groovy-4.0")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher:${junitVersion}")
}

allprojects {
  configurations.all {
    resolutionStrategy {
      failOnVersionConflict()
      force(dependencyVersions)
      eachDependency {
        val forcedVersion = dependencyVersionsByGroup[requested.group]
        if (forcedVersion != null) {
          useVersion(forcedVersion)
        }
      }
      cacheDynamicVersionsFor(Integer.parseInt(System.getenv("GRADLE_CACHE_DYNAMIC_SECONDS") ?: "0"), "seconds")
    }
  }
}

gradlePlugin {
  website.set("https://github.com/europace/artifact-version-gradle-plugin")
  vcsUrl.set("https://github.com/europace/artifact-version-gradle-plugin")
  plugins {
    create("artifactVersionPlugin") {
      id = "de.europace.gradle.artifact-version"
      displayName = "Plugin for consistent artifact version patterns"
      description = "Sets the project version to the current timestamp and writes the version to a text file when publishing artifacts"
      implementationClass = "de.europace.gradle.artifactversion.ArtifactVersionPlugin"
      tags.set(setOf("artifact", "version", "publishing"))
    }
  }
}

publishing {
  repositories {
    if (project.hasProperty("maven.publish.url") && project.hasProperty("maven.publish.username") && project.hasProperty("maven.publish.password")) {
      maven {
        name = "Maven"
        setUrl(project.property("maven.publish.url") as String)
        credentials.apply {
          username = project.property("maven.publish.username") as String
          password = project.property("maven.publish.password") as String
        }
      }
    }
  }
}

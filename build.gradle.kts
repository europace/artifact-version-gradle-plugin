import java.time.LocalDateTime.now
import java.time.format.DateTimeFormatter.ofPattern

buildscript {
  repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
  }
}

plugins {
  groovy
  id("org.jetbrains.kotlin.jvm") version "1.3.61"
  `maven-publish`
  `java-gradle-plugin`
  id("com.gradle.plugin-publish") version "0.10.1"
  id("com.github.ben-manes.versions") version "0.27.0"
}

group = "de.europace.gradle"
version = now().format(ofPattern("yyyy-MM-dd\'T\'HH-mm-ss"))
logger.lifecycle("version: $version")

val dependencyVersions = listOf<String>(
)

val dependencyVersionsByGroup = mapOf(
    "org.codehaus.groovy" to "2.5.9"
)

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
  mavenLocal()
  mavenCentral()
}

dependencies {
  implementation(gradleApi())
  testImplementation(localGroovy())
  testImplementation("org.spockframework:spock-core:1.3-groovy-2.5")
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
      cacheDynamicVersionsFor(0, "seconds")
    }
  }
}

pluginBundle {
  website = "https://github.com/europace/artifact-version-gradle-plugin"
  vcsUrl = "https://github.com/europace/artifact-version-gradle-plugin"
  tags = listOf("artifact", "version", "publishing")
}

gradlePlugin {
  plugins {
    create("artifactVersionPlugin") {
      id = "de.europace.gradle.artifact-version"
      displayName = "Plugin for consistent artifact version patterns"
      description = "Sets the project version to the current timestamp and writes the version to a text file when publishing artifacts"
      implementationClass = "de.europace.gradle.artifactversion.ArtifactVersionPlugin"
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

tasks {
  wrapper {
    gradleVersion = "5.6.3"
    distributionType = Wrapper.DistributionType.ALL
  }
}

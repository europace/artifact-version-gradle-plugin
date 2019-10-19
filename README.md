# Artifact Version Gradle Plugin

A Gradle plugin for consistent artifact version patterns.

Sets the project version to a timestamp and writes the version to a text file when publishing artifacts.

## Usage

The plugin is available on the official [Gradle plugin portal](https://plugins.gradle.org/).
It automatically configures all subprojects, so you only need to apply the plugin to your root Gradle project:

    plugins {
      id("de.europace.gradle.artifact-version") version "..."
    }

## Contributing

Please submit issues if you have any questions or suggestions regarding this plugin.
Code changes like bug fixes or new features can be proposed as pull requests.

## Publishing

Publishing requires username and API key from the project owner. Please see [the docs](https://plugins.gradle.org/docs/submit) for details.

package de.europace.gradle.artifactversion

import java.io.File
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

open class CreateArtifactVersionFileTask : DefaultTask() {

  @Input
  lateinit var artifactVersion: String

  @OutputFile
  lateinit var versionFile: File

  init {
    this.description = "Writes the current artifact version to a text file in the project directory."
    this.group = "versioning"

    // always run this task
    this.outputs.upToDateWhen { false }
  }

  @TaskAction
  fun action() {
    versionFile.writeText(artifactVersion)
  }
}

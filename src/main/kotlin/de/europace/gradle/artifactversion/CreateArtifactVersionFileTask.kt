package de.europace.gradle.artifactversion

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

open class CreateArtifactVersionFileTask : DefaultTask() {

  val versionFile: File
    @OutputFile
    get() = project.file(project.rootDir.toString() + "/" + project.name + "-version.txt")

  init {
    this.description = "Writes the current artifact version to a text file in the project directory."
    this.group = "versioning"

    // always run this task
    this.outputs.upToDateWhen { false }
  }

  @TaskAction
  fun action() {
    versionFile.writeText(project.version as String)
  }
}

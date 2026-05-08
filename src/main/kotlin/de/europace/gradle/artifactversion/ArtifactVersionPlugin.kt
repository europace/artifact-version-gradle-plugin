package de.europace.gradle.artifactversion

import java.time.LocalDateTime.now
import java.time.format.DateTimeFormatter.ofPattern
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.plugins.PublishingPlugin
import org.gradle.api.publish.plugins.PublishingPlugin.PUBLISH_LIFECYCLE_TASK_NAME

open class ArtifactVersionPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    if (project !== project.rootProject) {
      project.logger.warn("This plugin should only be applied to the root project")
    }

    val extension = project.extensions.create("artifactVersion", ArtifactVersionExtension::class.java)

    val timestamp = now()
    val defaultVersion = timestamp.format(ofPattern("yyyy-MM-dd\'T\'HH-mm-ss"))
    project.allprojects {
      it.logger.info("Setting version " + defaultVersion + " to project " + it.path)
      it.version = defaultVersion
    }

    project.pluginManager.apply(PublishingPlugin::class.java)
    project.afterEvaluate {
      val artifactVersion = if (extension.numericFormat.get()) {
        timestamp.format(ofPattern("yyyyMMddHHmmss")).also { v ->
          project.allprojects { it.version = v }
        }
      } else {
        defaultVersion
      }

      val versionFileTask = project.tasks.register(CREATE_ARTIFACT_VERSION_FILE_TASK_NAME, CreateArtifactVersionFileTask::class.java) {
        it.artifactVersion = artifactVersion
        it.versionFile = project.rootDir.resolve("${project.name}-version.txt")
      }
      project.tasks.findByName(PUBLISH_LIFECYCLE_TASK_NAME)?.dependsOn(versionFileTask)
    }
  }

  companion object {

    const val CREATE_ARTIFACT_VERSION_FILE_TASK_NAME = "createArtifactVersionFile"
  }
}

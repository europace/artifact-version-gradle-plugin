package de.europace.gradle.artifactversion

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.plugins.PublishingPlugin
import org.gradle.api.publish.plugins.PublishingPlugin.PUBLISH_LIFECYCLE_TASK_NAME
import java.time.LocalDateTime.now
import java.time.format.DateTimeFormatter.ofPattern

open class ArtifactVersionPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    if (project !== project.rootProject) {
      project.logger.warn("This plugin should only be applied to the root project")
    }

    val artifactVersion = now().format(ofPattern("yyyy-MM-dd\'T\'HH-mm-ss"))

    project.allprojects {
      it.logger.info("Setting version " + artifactVersion + " to project " + it.path)
      it.version = artifactVersion
    }

    project.pluginManager.apply(PublishingPlugin::class.java)
    project.afterEvaluate {
      val versionFileTask = project.tasks.create(CREATE_ARTIFACT_VERSION_FILE_TASK_NAME, CreateArtifactVersionFileTask::class.java)
      project.tasks.findByName(PUBLISH_LIFECYCLE_TASK_NAME)?.dependsOn(versionFileTask.name)
    }
  }

  companion object {

    const val CREATE_ARTIFACT_VERSION_FILE_TASK_NAME = "createArtifactVersionFile"
  }
}

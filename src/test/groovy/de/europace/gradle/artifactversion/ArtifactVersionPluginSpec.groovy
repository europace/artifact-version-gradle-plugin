package de.europace.gradle.artifactversion

import org.gradle.api.Project
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import spock.lang.Specification
import spock.lang.TempDir

import static de.europace.gradle.artifactversion.ArtifactVersionPlugin.CREATE_ARTIFACT_VERSION_FILE_TASK_NAME
import static org.gradle.api.publish.plugins.PublishingPlugin.PUBLISH_LIFECYCLE_TASK_NAME
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class ArtifactVersionPluginSpec extends Specification {

  @TempDir
  File testProjectDir

  String PLUGIN_ID = "de.europace.gradle.artifact-version"

  private File buildFile

  def setup() {
    buildFile = new File(testProjectDir, "build.gradle")
    buildFile.createNewFile()
  }

  def "performs createArtifactVersionTask if \"#taskName\" is executed"() {
    given:
    buildFile << """
        plugins {
            id '${PLUGIN_ID}' 
        }

        task logVersion {
           doFirst {
               logger.lifecycle("LOG_VERSION: \${project.version}")
               logger.lifecycle("LOG_FILENAME: \${((de.europace.gradle.artifactversion.CreateArtifactVersionFileTask) project.tasks.getByName(de.europace.gradle.artifactversion.ArtifactVersionPlugin.CREATE_ARTIFACT_VERSION_FILE_TASK_NAME)).versionFile}")
           }
        }
    """

    when:
    def result = GradleRunner.create()
        .withProjectDir(testProjectDir)
        .withPluginClasspath()
        .withArguments("logVersion", taskName)
        .forwardOutput()
        .build()

    then:
    result.task(":${CREATE_ARTIFACT_VERSION_FILE_TASK_NAME}").outcome == SUCCESS

    and:
    result.output.contains("LOG_VERSION: ")
    def projectVersion = getLoggedValue(result, "LOG_VERSION: ")
    projectVersion != Project.DEFAULT_VERSION

    and:
    result.output.contains("LOG_FILENAME: ")
    def versionFilename = getLoggedValue(result, "LOG_FILENAME: ")
    new File(versionFilename).readLines().find { it == projectVersion }

    where:
    taskName << [CREATE_ARTIFACT_VERSION_FILE_TASK_NAME, PUBLISH_LIFECYCLE_TASK_NAME]
  }

  def "creates version file in project root directory"() {
    given:
    def buildFile = new File(testProjectDir, "build.gradle")
    buildFile << """
      plugins {
        id 'de.europace.gradle.artifact-version'
      }
    """

    when:
    GradleRunner.create()
        .withProjectDir(testProjectDir)
        .withPluginClasspath()
        .withArguments("createArtifactVersionFile")
        .build()

    then:
    def versionFile = new File(testProjectDir, "${testProjectDir.name}-version.txt")
    versionFile.exists()
    new File(testProjectDir, "${testProjectDir.name}-version.txt").exists()

    and:
    !new File(testProjectDir, "build/${testProjectDir.name}-version.txt").exists()
  }

  String getLoggedValue(BuildResult result, String needle) {
    return result.output.readLines().find { it.contains(needle) }.substring(needle.length()).trim()
  }
}

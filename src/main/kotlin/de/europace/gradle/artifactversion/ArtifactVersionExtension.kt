package de.europace.gradle.artifactversion

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

abstract class ArtifactVersionExtension @Inject constructor(objects: ObjectFactory) {

  val numericFormat: Property<Boolean> = objects.property(Boolean::class.java).convention(false)
}

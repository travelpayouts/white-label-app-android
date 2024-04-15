package util

import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import configuration.BuildTypes

/**
 * Helper accessor for custom 'QA' build type
 * @see [BuildTypes]
 */
fun DependencyHandler.`qaImplementation`(dependencyNotation: Any): Dependency? =
    add("qaImplementation", dependencyNotation)

/**
 * Helper accessor for custom 'RC' build type
 * @see [BuildTypes]
 */
fun DependencyHandler.`rcImplementation`(dependencyNotation: Any): Dependency? =
    add("rcImplementation", dependencyNotation)
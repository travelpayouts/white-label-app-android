package buildconfig

import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.dsl.BuildType
import com.android.build.gradle.internal.dsl.ProductFlavor
import com.android.build.gradle.internal.dsl.SigningConfig
import configuration.BuildTypes
import configuration.ProductFlavors
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project

/**
 * Build configuration for android library module
 *
 * @author Anatoliy Shulipov (as@cleverpumpkin.ru)
 */
class LibraryModuleBuildConfiguration(
    project: Project,
    libraryExtension: LibraryExtension
) : BaseBuildConfiguration(project, libraryExtension) {

    override val manifestPlaceholders: Map<String, Any>? = null

    override fun configureSigningConfigs(signingConfigs: NamedDomainObjectContainer<SigningConfig>) =
        Unit

    override fun configureBuildTypes(buildTypes: NamedDomainObjectContainer<BuildType>) {
        val projectProguardFiles = buildProguardFiles().asArray

        buildTypes.apply {
            maybeCreate(BuildTypes.DEBUG.name).apply {
                isDebuggable = true
            }
            maybeCreate(BuildTypes.QA.name).apply {
                isDebuggable = true
                isMinifyEnabled = true
                proguardFiles(*projectProguardFiles)
            }
            maybeCreate(BuildTypes.RC.name).apply {
                isDebuggable = false
                isMinifyEnabled = true
                proguardFiles(*projectProguardFiles)
            }
            getByName(BuildTypes.RELEASE.name).apply {
                isDebuggable = false
                isMinifyEnabled = true
                proguardFiles(*projectProguardFiles)
            }
        }
    }

    override fun configureProductFlavors(
        productFlavors: NamedDomainObjectContainer<ProductFlavor>,
        buildTypes: NamedDomainObjectContainer<BuildType>
    ) {
        productFlavors.apply {
            maybeCreate(ProductFlavors.BASIC).apply {
                dimension = "products"
            }


        }
    }

}
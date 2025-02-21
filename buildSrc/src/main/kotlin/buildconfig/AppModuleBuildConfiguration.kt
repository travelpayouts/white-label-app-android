package buildconfig

import com.android.build.api.dsl.ApplicationDefaultConfig
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.android.build.gradle.internal.dsl.BuildType
import com.android.build.gradle.internal.dsl.ProductFlavor
import com.android.build.gradle.internal.dsl.SigningConfig
import configuration.ApplicationVersions
import configuration.BuildTypes
import configuration.Config
import configuration.ProductFlavors
import configuration.SigningConfigs
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import java.io.File
import java.util.Collections.emptyMap
import java.util.Properties

/**
 * Build configuration for android app module
 *
 * @author Anatoliy Shulipov (as@cleverpumpkin.ru)
 */

class AppModuleBuildConfiguration(
    private val project: Project,
    private val appExtension: BaseAppModuleExtension
) : BaseBuildConfiguration(project, appExtension) {

    override val manifestPlaceholders: Map<String, Any> = emptyMap()

    override fun configure() {
        super.configure()
        with(appExtension) {
            defaultConfig {
                setupMainArguments(this)
            }
        }
    }

    private fun setupMainArguments(defaultConfig: ApplicationDefaultConfig) {
        defaultConfig.apply {
            applicationId = Config.APPLICATION_ID
            versionCode = Config.VERSION_CODE
            versionName = Config.VERSION_NAME
        }
    }

    override fun configureBuildTypes(buildTypes: NamedDomainObjectContainer<BuildType>) {
        val projectProguardFiles = buildProguardFiles().asArray

        buildTypes.apply {
            getByName(BuildTypes.DEBUG.name).apply {
                isDebuggable = true
                isMinifyEnabled = false
                applicationIdSuffix = ".debug"
                versionNameSuffix = ApplicationVersions.VERSION_NAME_SUFFIX
                signingConfig = appExtension.signingConfigs.getByName(SigningConfigs.Debug.NAME)
                setManifestPlaceholders(
                    mapOf("appIconRes" to "@mipmap/ta_ic_launcher")
                )
            }

            maybeCreate(BuildTypes.QA.name).apply {
                isDebuggable = true
                isMinifyEnabled = true
                applicationIdSuffix = ".debug"
                versionNameSuffix = ApplicationVersions.VERSION_NAME_SUFFIX
                signingConfig = appExtension.signingConfigs.getByName(SigningConfigs.Debug.NAME)
                proguardFiles(*projectProguardFiles)
                setManifestPlaceholders(
                    mapOf("appIconRes" to "@mipmap/ta_ic_launcher")
                )
            }

            maybeCreate(BuildTypes.RC.name).apply {
                isDebuggable = false
                isMinifyEnabled = true
                versionNameSuffix = ApplicationVersions.VERSION_NAME_SUFFIX
                signingConfig = appExtension.signingConfigs.getByName(SigningConfigs.Release.NAME)
                proguardFiles(*projectProguardFiles)
                setManifestPlaceholders(
                    mapOf("appIconRes" to "@mipmap/ta_ic_launcher")
                )
            }

            getByName(BuildTypes.RELEASE.name).apply {
                isDebuggable = false
                isMinifyEnabled = true
                isShrinkResources = false
                signingConfig = appExtension.signingConfigs.getByName(SigningConfigs.Release.NAME)
                proguardFiles(*projectProguardFiles)
                setMatchingFallbacks(BuildTypes.RC.name)
                setManifestPlaceholders(
                    mapOf("appIconRes" to "@mipmap/ta_ic_launcher")
                )
            }
        }
    }

    override fun configureSigningConfigs(signingConfigs: NamedDomainObjectContainer<SigningConfig>) {
        val path = project.rootDir

        val reader = project.parent?.file("signing.properties")?.reader() ?: throw IllegalStateException("No signing config provided!")

        val signingProps = Properties().apply {
            load(reader)
        }

        reader.close()

        signingConfigs.maybeCreate(SigningConfigs.Debug.NAME)
            .setStoreFile(File("$path/${signingProps.getProperty("KEYSTORE_FILE_DEBUG")}"))
            .setStorePassword(signingProps.getProperty("KEYSTORE_FILE_PASSWORD_DEBUG"))
            .setKeyAlias(signingProps.getProperty("KEY_ALIAS_DEBUG"))
            .setKeyPassword(signingProps.getProperty("KEY_PASSWORD_DEBUG"))

        signingConfigs.maybeCreate(SigningConfigs.Release.NAME)
            .setStoreFile(File("$path/${signingProps.getProperty("KEYSTORE_FILE_RELEASE")}"))
            .setStorePassword(signingProps.getProperty("KEYSTORE_FILE_PASSWORD_RELEASE"))
            .setKeyAlias(signingProps.getProperty("KEY_ALIAS_RELEASE"))
            .setKeyPassword(signingProps.getProperty("KEY_PASSWORD_RELEASE"))
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
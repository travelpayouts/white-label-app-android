package buildconfig

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.dsl.BuildType
import com.android.build.gradle.internal.dsl.ProductFlavor
import com.android.build.gradle.internal.dsl.SigningConfig
import configuration.Config
import model.ProguardFiles
import org.gradle.api.JavaVersion
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import java.io.File

/**
 * Base build configuration for android gradle module
 *
 * @author Anatoliy Shulipov (as@cleverpumpkin.ru)
 */
abstract class BaseBuildConfiguration(
    private val project: Project,
    private val baseExtension: BaseExtension,
) {

    abstract val manifestPlaceholders: Map<String, Any>?

    open fun configure() {
        with(baseExtension) {

            defaultConfig {
                minSdk = Config.minSdk
                setCompileSdkVersion(Config.compileSdk)
                targetSdk = Config.targetSdk

                vectorDrawables.useSupportLibrary = Config.SUPPORT_LIBRARY_VECTOR_DRAWABLES
                manifestPlaceholders.let(::addManifestPlaceholders)
            }

            lintOptions {
                isCheckReleaseBuilds = false
                isAbortOnError = true
                fatal("StopShip")
            }

            sourceSets {
                getByName("main").java.srcDirs("src/main/kotlin")
                getByName("debug").java.srcDirs("src/debug/kotlin")
                getByName("release").java.srcDirs("src/release/kotlin")
            }

            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
                isCoreLibraryDesugaringEnabled = true
            }

            viewBinding.isEnabled = true

            buildFeatures.buildConfig = true

            configureSigningConfigs(signingConfigs)
            configureBuildTypes(buildTypes)

            flavorDimensions("products")
            configureProductFlavors(productFlavors, buildTypes)
        }
    }

    abstract fun configureSigningConfigs(signingConfigs: NamedDomainObjectContainer<SigningConfig>)

    abstract fun configureBuildTypes(buildTypes: NamedDomainObjectContainer<out BuildType>)

    protected fun buildProguardFiles(): ProguardFiles {
        val rulesFile = File("proguard-rules.pro")
        val defaultFile =
            com.android.build.gradle.ProguardFiles.getDefaultProguardFile(
                "proguard-android.txt",
                project.layout.buildDirectory
            )
        return ProguardFiles(
            proguardRulesFile = rulesFile,
            defaultProguardFile = defaultFile
        )
    }

    abstract fun configureProductFlavors(
        productFlavors: NamedDomainObjectContainer<out ProductFlavor>,
        buildTypes: NamedDomainObjectContainer<out BuildType> ,
    )
}

fun BaseExtension.setCompose(compilerVersion: String) {
    buildFeatures.compose = true

    composeOptions {
        //kotlinCompilerExtensionVersion = compilerVersion
    }
}
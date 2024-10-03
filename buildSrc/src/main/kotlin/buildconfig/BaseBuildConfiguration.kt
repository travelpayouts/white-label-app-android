package buildconfig

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.dsl.BuildType
import com.android.build.gradle.internal.dsl.JavaCompileOptions
import com.android.build.gradle.internal.dsl.ProductFlavor
import com.android.build.gradle.internal.dsl.SigningConfig
import configuration.Config
import model.ProguardFiles
import org.gradle.api.Action
import org.gradle.api.JavaVersion
import org.gradle.api.NamedDomainObjectContainer
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import java.io.File

/**
 * Base build configuration for android gradle module
 *
 * @author Anatoliy Shulipov (as@cleverpumpkin.ru)
 */
abstract class BaseBuildConfiguration(
    private val project: Project,
    private val baseExtension: BaseExtension
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
                isAbortOnError = true
                fatal("StopShip")
            }

            sourceSets {
                getByName("main").java.srcDirs("src/main/kotlin")
                getByName("debug").java.srcDirs("src/debug/kotlin")
                getByName("release").java.srcDirs("src/release/kotlin")
            }

            compileOptions {
                isCoreLibraryDesugaringEnabled = true
            }

            (this as org.gradle.api.plugins.ExtensionAware).extensions.configure("kotlinOptions", Action<KotlinJvmOptions> {
                jvmTarget = "11"
            })

            viewBinding.isEnabled = true

            buildFeatures.buildConfig = true

            configureSigningConfigs(signingConfigs)
            configureBuildTypes(buildTypes)

            flavorDimensions("products")
            configureProductFlavors(productFlavors, buildTypes)
        }
    }

    abstract fun configureSigningConfigs(signingConfigs: NamedDomainObjectContainer<SigningConfig>)

    abstract fun configureBuildTypes(buildTypes: NamedDomainObjectContainer<BuildType>)

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
        productFlavors: NamedDomainObjectContainer<ProductFlavor>,
        buildTypes: NamedDomainObjectContainer<BuildType>
    )
}

fun BaseExtension.setCompose(compilerVersion: String) {
    buildFeatures.compose = true

    composeOptions {
        //kotlinCompilerExtensionVersion = compilerVersion
    }
}
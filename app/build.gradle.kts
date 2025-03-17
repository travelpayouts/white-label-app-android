import buildconfig.AppModuleBuildConfiguration
import com.project.starter.easylauncher.plugin.EasyLauncherConfig
import configuration.BuildModules
import java.io.FileInputStream
import java.util.Properties
import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id(configuration.BuildPlugins.ANDROID_APPLICATION)
    id(configuration.BuildPlugins.KOTLIN_ANDROID)
    id(configuration.BuildPlugins.KOTLIN_KAPT)
    id(configuration.BuildPlugins.KOTLIN_PARCELIZE)
    id(configuration.BuildPlugins.NAVIGATION_SAFE_ARGS)
    id(configuration.BuildPlugins.FIREBASE_CRASHLYTICS)
    id(configuration.BuildPlugins.GOOGLE_SERVICES)
    id(configuration.BuildPlugins.EASY_LAUNCHER) version "6.2.0"


}

private val FILE_NAME = "handling_link.properties"
private val PROP_HANDLING_LINK = "handlingLink"

private val prop: Properties = Properties().apply {
    val fis = FileInputStream(FILE_NAME)
    load(fis)
    fis.close()
}


android {
    namespace = "com.travelapp"
    AppModuleBuildConfiguration(project, appExtension = this).configure()

    defaultConfig {
        manifestPlaceholders["custom_app_id"] = configuration.ApplicationVersions.APPLICATION_ID
        manifestPlaceholders["intent_filter"] = prop.getProperty(PROP_HANDLING_LINK)
        archivesName = "$applicationId-$versionName"
    }

    kotlin {
        jvmToolchain(11)
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(11))
        }
    }
//    disableFirebasePerfForDebug(buildTypes)
}

easylauncher {
    buildTypes {
        create(configuration.BuildTypes.DEBUG.name) {
            configure(
                listOf("@mipmap/ta_ic_launcher"),
                configuration.BuildTypes.DEBUG.ribbonColor
            )
        }
        create(configuration.BuildTypes.QA.name) {
            configure(
                listOf("@mipmap/ta_ic_launcher"),
                configuration.BuildTypes.QA.ribbonColor
            )
        }
        create(configuration.BuildTypes.RC.name) {
            configure(
                listOf("@mipmap/ta_ic_launcher"),
                configuration.BuildTypes.RC.ribbonColor
            )
        }
        create(configuration.BuildTypes.RELEASE.name) {
            enable(false)
        }
    }
}



dependencies {
    coreLibraryDesugaring(Tools.DESUGAR_JDK_LIBS)
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // Travel SDK
    implementation(project(BuildModules.SDK))
    implementation(project(BuildModules.Common.DEBUG))

    // AndroidX
    implementation(AndroidX.CORE)
    implementation(AndroidX.FRAGMENT)
    implementation(AndroidX.APPCOMPAT)
    implementation(AndroidX.RECYCLER_VIEW)
    implementation(AndroidX.CONSTRAINT_LAYOUT)

    implementation(LifeCycle.LIFECYCLE_EXTENSIONS)

    // Navigation
    implementation(Navigation.UI)
    implementation(Navigation.FRAGMENT)

    // Maps
    implementation(Google.MAPS_SERVICES)
    implementation(Google.MAPS_UTILS)
    implementation(Google.MAPS_UTILS_KTX)
    implementation(Google.PLAY_SERVICES_MAPS)
    implementation(Google.PLAY_SERVICES_LOCATION)

    // Retrofit
    implementation(platform(OkHttp.BOM))
    implementation(OkHttp.LIBRARY)
    implementation(OkHttp.LOGGING_INTERCEPTOR)
    implementation(Retrofit.LIBRARY)
    implementation(Retrofit.GSON_CONVERTER)
    implementation(Retrofit.SCALAR_CONVERTER)

    // UI
    implementation(MaterialComponents.LIBRARY)
    implementation(UI.VIEWBINDING_PROPERTY_DELEGATE)
    implementation(Tools.CRUNCHY_CALENDAR)
    implementation(Skeleton.LIBRARY)
    implementation(Lottie.LIBRARY)
    implementation(AdapterDelegate.CORE)
    implementation(AdapterDelegate.DSL)
    implementation(AdapterDelegate.LAYOUT_CONTAINER)
    implementation(AdapterDelegate.VIEW_BINDING)

    //Flow binding
    implementation(FlowBinding.CORE)
    implementation(FlowBinding.MATERIAL)
    implementation(FlowBinding.PLATFORM)
    implementation(FlowBinding.APPCOMPAT)
    implementation(FlowBinding.VIEWPAGER)
    implementation(FlowBinding.RECYCLERVIEW)


    //Coil
    implementation(Coil.BASE)
    implementation(Coil.LIBRARY)

    // FIrebase
    implementation(platform(Firebase.BOM))
    implementation(Firebase.ANALYTICS)
    implementation(Firebase.CRASHLYTICS)
    implementation(Firebase.CLOUD_MESSAGING)

    // Dagger
    implementation(Dagger2.LIBRARY)
    kapt(Dagger2.COMPILER)
    compileOnly(Dagger2.ANNOTATIONS)

    //Tools
    implementation(Tools.TIMBER)
    implementation(Tools.INSETTER)
    implementation(Tools.GSON)
    implementation(Tools.SEISMIC)

    implementation(Tools.APPODEAL) { exclude("com.android.billingclient", "billing") }

    // AppsFlyer
    implementation(AppsFlyer.LIBRARY)

    //InAppReview
    implementation(Tools.IN_APP_REVIEW)
    implementation(Tools.IN_APP_REVIEW_KTX)

}

//evaluationDependsOn(BuildModules.Config.LIBRARY)

//fun disableFirebasePerfForDebug(buildTypes: NamedDomainObjectContainer<com.android.build.gradle.internal.dsl.BuildType>) {
//    val debug = (buildTypes.getByName(configuration.BuildTypes.DEBUG.name) as ExtensionAware)
//    val firebasePerf =
//        debug.extensions["FirebasePerformance"] as com.google.firebase.perf.plugin.FirebasePerfExtension
//    firebasePerf.setInstrumentationEnabled(false)
//}

fun EasyLauncherConfig.configure(icons: List<String>, ribbonColor: String) {
    setIconNames(icons)
    filters(
        chromeLike(
            label = configuration.ApplicationVersions.FULL_APP_VERSION_NAME,
            labelPadding = 13,
            textSizeRatio = 0.12f,
            ribbonColor = ribbonColor
        )
    )
}

private fun DependencyHandlerScope.appodealNetworkWithoutAdmob() {
    implementation(Tools.APPODEAL_AMAZON)
    implementation(Tools.APPODEAL_APPLOVIN)
    implementation(Tools.APPODEAL_APPLOVIN_MAX)
    implementation(Tools.APPODEAL_BIDMACHINE)
    implementation(Tools.APPODEAL_BIDON)
    implementation(Tools.APPODEAL_BIGO_ADS)
    implementation(Tools.APPODEAL_DT_EXCHANGE)
    implementation(Tools.APPODEAL_IAB)
    implementation(Tools.APPODEAL_INMOBI)
    implementation(Tools.APPODEAL_IRONSOURCE)
    implementation(Tools.APPODEAL_META)
    implementation(Tools.APPODEAL_MINTEGRAL)
    implementation(Tools.APPODEAL_MY_TARGET) {
        exclude("com.android.billingclient", "billing")
    }
    implementation(Tools.APPODEAL_PANGLE)
    implementation(Tools.APPODEAL_UNITY_ADS)
    implementation(Tools.APPODEAL_VUNGLE)
    implementation(Tools.APPODEAL_YANDEX)
}

import buildconfig.AppModuleBuildConfiguration
import com.project.starter.easylauncher.plugin.EasyLauncherConfig
import configuration.BuildModules
import java.io.FileInputStream
import java.util.Properties

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
    }

    applicationVariants.all {
        outputs.all {
            val output = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
            output.outputFileName = "$applicationId-${versionName}.apk" // Or .aab for app bundles
        }
    }


    kotlin {
        jvmToolchain(17)
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
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
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // Travel SDK
    implementation(project(BuildModules.SDK))
    implementation(project(BuildModules.Common.DEBUG))

    // AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.constraintlayout)

    implementation(libs.lifecycle.extensions)

    // Navigation
    implementation(libs.navigation.ui)
    implementation(libs.navigation.fragment)

    // Maps
    implementation(libs.maps.services)
    implementation(libs.maps.utils)
    implementation(libs.maps.utils.ktx)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)

    // Retrofit
    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
    implementation(libs.loggin.interceptor)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.retrofit.converter.scalars)

    // UI
    implementation(libs.material.components)
    implementation(libs.viewbinding.property.delegate)
    implementation(libs.crunchycalendar)
    implementation(libs.skeleton)
    implementation(libs.lottie)
    implementation(libs.adapterdelegate.core)
    implementation(libs.adapterdelegate.dsl)
    implementation(libs.adapterdelegate.layout.container)
    implementation(libs.adapterdelegate.view.binding)

    //Flow binding
    implementation(libs.flowbinding.core)
    implementation(libs.flowbinding.material)
    implementation(libs.flowbinding.platform)
    implementation(libs.flowbinding.appcompat)
    implementation(libs.flowbinding.viewpager)
    implementation(libs.flowbinding.recyclerview)

    //Coil
    implementation(libs.coil.base)
    implementation(libs.coil)

    // FIrebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.messaging)

    // Dagger
    implementation(libs.dagger)
    kapt(libs.dagger.compiler)
    compileOnly(libs.dagger.annotation)

    //Tools
    implementation(libs.timberkt)
    implementation(libs.insetter)
    implementation(libs.gson)
    implementation(libs.seismic)

    implementation(libs.appodeal) { exclude("com.android.billingclient", "billing") }

    // AppsFlyer
    implementation(libs.appsflyer)

    //InAppReview
    implementation(libs.app.review)
    implementation(libs.app.review.ktx)

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
    implementation(libs.appodeal.amazon)
    implementation(libs.appodeal.applovin)
    implementation(libs.appodeal.applovin.max)
    implementation(libs.appodeal.bidmachine)
    implementation(libs.appodeal.bidon)
    implementation(libs.appodeal.bigo.ads)
    implementation(libs.appodeal.dt.exchange)
    implementation(libs.appodeal.iab)
    implementation(libs.appodeal.inmobi)
    implementation(libs.appodeal.ironsource)
    implementation(libs.appodeal.meta)
    implementation(libs.appodeal.mintegral)
    implementation(libs.appodeal.my.target) {
        exclude("com.android.billingclient", "billing")
    }
    implementation(libs.appodeal.pangle)
    implementation(libs.appodeal.unity.ads)
    implementation(libs.appodeal.vungle)
    implementation(libs.appodeal.yandex)
}

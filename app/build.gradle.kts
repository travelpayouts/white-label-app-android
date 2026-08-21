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

// Режим рекламы из advertising.properties. Чтение проверяет, что файл на месте
// и не разошёлся с config/app_config.json, иначе останавливает сборку.
private val adsMode: String = appconfig.AdvertisingMode.read(project)

// Строгие проверки вешаются на граф задач: командную строку разбирать нельзя,
// её формы (сокращения, опции задач) обходят любую самодельную эвристику
private val advertisingGuards = appconfig.AdvertisingMode.registerGuards(project)

private val prop: Properties = Properties().apply {
    // Путь от корня проекта, а не от рабочего каталога JVM. Относительное имя
    // резолвится от каталога запуска и ломается вне корня. Одного этого места
    // мало: configuration/ApplicationVersions читает app_version.properties так
    // же, см. TAAD-1239
    val fis = FileInputStream(rootProject.file(FILE_NAME))
    load(fis)
    fis.close()
}


// Проверка, что рекламные адаптеры реально доехали в сборку.
// Запуск: ./gradlew verifyAdvertisingWiring -PadsMode=appodeal_admob
verification.AdvertisingWiring.register(project)

android {
    namespace = "com.travelapp"
    AppModuleBuildConfiguration(project, appExtension = this).configure()

    defaultConfig {
        manifestPlaceholders["custom_app_id"] = configuration.ApplicationVersions.APPLICATION_ID
        manifestPlaceholders["intent_filter"] = prop.getProperty(PROP_HANDLING_LINK)
    }

    packaging {
        resources {
            // okhttp 5.3.2 brings org.jspecify:jspecify, and both jars carry
            // this file. Without the exclude the build fails on
            // mergeJavaResource. The same conflict is documented for
            // integrators in the SDK integration guide.
            excludes += "/META-INF/versions/9/OSGI-INF/MANIFEST.MF"
        }
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
    // Required by the SDK: it is compiled with core library desugaring, so the
    // app that embeds it has to enable it too.
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // Travel SDK. The :travel-sdk module declares every dependency the SDK
    // needs and exports them, so they are not repeated here. Versions come
    // from there and match the SDK build — before, this module listed them
    // again and they drifted: okhttp 5.1.0 against 5.3.2, firebase-bom 33.16.0
    // against 34.9.0, dagger 2.56.2 against 2.59.1 and a dozen more.
    implementation(project(BuildModules.SDK))
    implementation(project(BuildModules.Common.DEBUG))

    // Used by this app itself, not by the SDK
    implementation(libs.firebase.messaging)
    implementation(libs.flowbinding.viewpager)
    implementation(libs.seismic)

    // Dagger: the app has its own graph, so it needs the compiler. The runtime
    // library comes with the SDK.
    kapt(libs.dagger.compiler)
    compileOnly(libs.dagger.annotation)

    // Реклама. Что подключать, решает блок advertising в config/app_config.json:
    // задача parseConfig записывает режим в advertising.properties, а список
    // зависимостей лежит здесь и генератором не правится.
    when (adsMode) {
        appconfig.AdvertisingMode.NONE -> Unit
        appconfig.AdvertisingMode.APPODEAL -> appodealNetworks()
        appconfig.AdvertisingMode.APPODEAL_ADMOB -> {
            appodealNetworks()
            implementation(libs.appodeal.admob)
        }
    }
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

// Набор рекламных сетей Appodeal. Ядро приезжает из модуля travel-sdk, здесь
// объявляются только адаптеры. Версии — из каталога, парные ядру.
private fun DependencyHandlerScope.appodealNetworks() {
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

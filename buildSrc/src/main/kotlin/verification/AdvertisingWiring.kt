package verification

import org.gradle.api.GradleException
import org.gradle.api.Project

/**
 * Checks the result rather than the intent: whether the Appodeal ad adapters
 * actually reached the resolved dependency tree of the app.
 *
 * Why this is a separate task. The template ships with empty ad keys, so the
 * "ads on" branch never gets built before it reaches you. This task gives a way
 * to build that branch without editing config/app_config.json:
 *
 *     ./gradlew verifyAdvertisingWiring -PadsMode=appodeal_admob
 *
 * Modes: none (no ads), appodeal (adapters without AdMob), appodeal_admob
 * (adapters together with AdMob).
 *
 * With your own keys filled in, run the task without -PadsMode: then it checks
 * your configuration instead of a substituted one.
 */
object AdvertisingWiring {

    private const val NETWORKS_GROUP = "com.appodeal.ads.sdk.networks"
    private const val CORE_GROUP = "com.appodeal.ads.sdk"
    private const val ADMOB_MODULE = "admob"
    private const val CONFIGURATION = "basicReleaseRuntimeClasspath"

    /**
     * The set of networks that must arrive when advertising is on. Keep it in
     * sync with appodealNetworks() in app/build.gradle.kts: a difference means
     * some of the networks silently dropped out of monetisation.
     */
    private val EXPECTED_NETWORKS = setOf(
        "amazon", "applovin", "applovin_max", "bidmachine", "bidon", "bigo_ads",
        "dt_exchange", "iab", "inmobi", "ironsource", "meta", "mintegral",
        "my_target", "pangle", "unity_ads", "vungle", "yandex"
    )

    // The modes are repeated here so that the list sits next to the check that uses it
    private const val MODE_NONE = "none"
    private const val MODE_APPODEAL = "appodeal"
    private const val MODE_APPODEAL_ADMOB = "appodeal_admob"
    private val MODES = listOf(MODE_NONE, MODE_APPODEAL, MODE_APPODEAL_ADMOB)

    fun register(project: Project) = with(project) {
        tasks.register("verifyAdvertisingWiring") {
            group = "verification"
            description = "Checks that the Appodeal ad adapters reached the dependency tree"

            // The same source the dependencies in app/build.gradle.kts use. This task used to
            // read -PadsMode on its own, which made the parameter change what the check
            // expected without changing what was actually built.
            val mode = appconfig.AdvertisingMode.read(project)
            val configuration = configurations.named(CONFIGURATION)

            doLast {
                if (mode !in MODES) {
                    throw GradleException(
                        "Unknown advertising mode '$mode'. Valid values: ${MODES.joinToString(", ")}"
                    )
                }

                // Resolve the graph and take everything belonging to Appodeal out of it
                val modules = configuration.get().incoming.resolutionResult.allComponents
                    .mapNotNull { it.moduleVersion }

                val adapters = modules.filter { it.group == NETWORKS_GROUP }
                val cores = modules.filter { it.group == CORE_GROUP && it.name == "core" }
                val hasAdmob = adapters.any { it.name == ADMOB_MODULE }

                val problems = mutableListOf<String>()

                if (mode == MODE_NONE) {
                    // A partner without ads should not carry tens of megabytes of adapters
                    if (adapters.isNotEmpty()) {
                        problems += "the mode is none, yet the build carries ${adapters.size} ad " +
                            "adapters: " + adapters.joinToString(", ") { it.name }
                    }
                } else {
                    if (adapters.isEmpty()) {
                        problems += "the mode is $mode, yet not a single adapter of group " +
                            "$NETWORKS_GROUP is in $CONFIGURATION. The keys are filled in and no " +
                            "ads will be shown. The dependency list lives in the when block of " +
                            "app/build.gradle.kts - check that the branch for this mode is intact"
                    } else {
                        // Checking for "at least one" is not enough: sixteen adapters out of
                        // seventeen could go missing unnoticed
                        val actual = adapters.map { it.name }.toSet() - ADMOB_MODULE
                        val missing = EXPECTED_NETWORKS - actual
                        if (missing.isNotEmpty()) {
                            problems += "adapters are missing (${missing.size} of " +
                                "${EXPECTED_NETWORKS.size}): ${missing.sorted().joinToString(", ")}"
                        }
                        // The check is symmetric: a network added to the build and forgotten in
                        // the list would otherwise stay out of control forever
                        val unexpected = actual - EXPECTED_NETWORKS
                        if (unexpected.isNotEmpty()) {
                            problems += "the build carries adapters that are not in the expected " +
                                "list: ${unexpected.sorted().joinToString(", ")}. Add them to " +
                                "EXPECTED_NETWORKS, or nobody will notice when they disappear"
                        }
                    }
                    if (mode == MODE_APPODEAL_ADMOB && !hasAdmob) {
                        problems += "the mode is $mode, yet the adapter " +
                            "$NETWORKS_GROUP:$ADMOB_MODULE is not in the build"
                    }
                    if (mode == MODE_APPODEAL && hasAdmob) {
                        problems += "the mode is $mode, yet the AdMob adapter is wired in"
                    }

                    // Version pairing: on the Appodeal 3.x branch the adapters are versioned
                    // together with the core, core X.Y.Z goes with adapters X.Y.Z.N
                    val coreVersion = cores.firstOrNull()?.version
                    if (coreVersion == null && adapters.isNotEmpty()) {
                        problems += "there are adapters, but no $CORE_GROUP:core in the build"
                    } else if (coreVersion != null) {
                        val mismatched = adapters.filterNot { it.version.startsWith("$coreVersion.") }
                        if (mismatched.isNotEmpty()) {
                            problems += "adapter versions are not paired with core $coreVersion: " +
                                mismatched.joinToString(", ") { "${it.name}:${it.version}" }
                        }
                    }
                }

                if (problems.isNotEmpty()) {
                    throw GradleException(
                        "The advertising check failed (mode=$mode):\n" +
                            problems.joinToString("\n") { "  - $it" }
                    )
                }

                logger.lifecycle(
                    "verifyAdvertisingWiring: mode=$mode, ${adapters.size} adapters, core " +
                        "${cores.firstOrNull()?.version ?: "absent"} - all good"
                )
            }
        }
    }
}

package verification

import org.gradle.api.GradleException
import org.gradle.api.Project

/**
 * Проверяет не намерение, а результат: доехали ли рекламные адаптеры Appodeal
 * в собранное дерево зависимостей приложения.
 *
 * Зачем это отдельная задача. Шаблон уезжает партнёру с пустыми ключами рекламы —
 * этого требует check-no-secrets.sh перед передачей. Значит ветка «реклама
 * включена» на нашей стороне не собирается никогда, и поломка в ней остаётся
 * незамеченной до тех пор, пока о ней не сообщит партнёр. Задача даёт способ
 * собрать эту ветку, не трогая отгружаемый config/app_config.json:
 *
 *     ./gradlew verifyAdvertisingWiring -PadsMode=appodeal_admob
 *
 * Режимы: none (рекламы нет), appodeal (адаптеры без AdMob),
 * appodeal_admob (адаптеры вместе с AdMob).
 */
object AdvertisingWiring {

    private const val NETWORKS_GROUP = "com.appodeal.ads.sdk.networks"
    private const val CORE_GROUP = "com.appodeal.ads.sdk"
    private const val ADMOB_MODULE = "admob"
    private const val CONFIGURATION = "basicReleaseRuntimeClasspath"

    /**
     * Набор сетей, который обязан доехать при включённой рекламе. Держать
     * синхронным с appodealNetworks() в app/build.gradle.kts: расхождение
     * означает, что часть сетей молча выпала из монетизации.
     */
    private val EXPECTED_NETWORKS = setOf(
        "amazon", "applovin", "applovin_max", "bidmachine", "bidon", "bigo_ads",
        "dt_exchange", "iab", "inmobi", "ironsource", "meta", "mintegral",
        "my_target", "pangle", "unity_ads", "vungle", "yandex"
    )

    // Режимы вынесены сюда, чтобы список был в одном месте с проверкой
    private const val MODE_NONE = "none"
    private const val MODE_APPODEAL = "appodeal"
    private const val MODE_APPODEAL_ADMOB = "appodeal_admob"
    private val MODES = listOf(MODE_NONE, MODE_APPODEAL, MODE_APPODEAL_ADMOB)

    fun register(project: Project) = with(project) {
        tasks.register("verifyAdvertisingWiring") {
            group = "verification"
            description = "Проверяет, что рекламные адаптеры Appodeal доехали в дерево зависимостей"

            // Тот же источник, что и у зависимостей в app/build.gradle.kts.
            // Раньше здесь читался свой -PadsMode, из-за чего параметр менял
            // только ожидание проверки, а не то, что реально собирается.
            val mode = appconfig.AdvertisingMode.read(project)
            val configuration = configurations.named(CONFIGURATION)

            doLast {
                if (mode !in MODES) {
                    throw GradleException(
                        "Неизвестный режим adsMode=$mode. Допустимые: ${MODES.joinToString(", ")}"
                    )
                }

                // Резолвим граф и берём оттуда всё, что относится к Appodeal
                val modules = configuration.get().incoming.resolutionResult.allComponents
                    .mapNotNull { it.moduleVersion }

                val adapters = modules.filter { it.group == NETWORKS_GROUP }
                val cores = modules.filter { it.group == CORE_GROUP && it.name == "core" }
                val hasAdmob = adapters.any { it.name == ADMOB_MODULE }

                val problems = mutableListOf<String>()

                if (mode == MODE_NONE) {
                    // Партнёр без рекламы не должен тащить десятки мегабайт адаптеров
                    if (adapters.isNotEmpty()) {
                        problems += "adsMode=none, но в сборке ${adapters.size} рекламных адаптеров: " +
                            adapters.joinToString(", ") { it.name }
                    }
                } else {
                    if (adapters.isEmpty()) {
                        problems += "adsMode=$mode, но ни одного адаптера группы $NETWORKS_GROUP " +
                            "в $CONFIGURATION нет. Ключи заполнены, реклама показываться не будет. " +
                            "Запустите ./gradlew parseConfig"
                    } else {
                        // Проверять «есть хотя бы один» мало: пропажа шестнадцати
                        // адаптеров из семнадцати прошла бы незамеченной
                        val missing = EXPECTED_NETWORKS - adapters.map { it.name }.toSet()
                        if (missing.isNotEmpty()) {
                            problems += "не хватает адаптеров (${missing.size} из " +
                                "${EXPECTED_NETWORKS.size}): ${missing.sorted().joinToString(", ")}"
                        }
                    }
                    if (mode == MODE_APPODEAL_ADMOB && !hasAdmob) {
                        problems += "adsMode=$mode, но адаптера $NETWORKS_GROUP:$ADMOB_MODULE нет"
                    }
                    if (mode == MODE_APPODEAL && hasAdmob) {
                        problems += "adsMode=$mode, но адаптер AdMob подключён"
                    }

                    // Парность версий: на ветке Appodeal 3.x адаптеры версионируются
                    // вместе с ядром, core X.Y.Z идёт с адаптерами X.Y.Z.N
                    val coreVersion = cores.firstOrNull()?.version
                    if (coreVersion == null && adapters.isNotEmpty()) {
                        problems += "адаптеры есть, а ядра $CORE_GROUP:core в сборке нет"
                    } else if (coreVersion != null) {
                        val mismatched = adapters.filterNot { it.version.startsWith("$coreVersion.") }
                        if (mismatched.isNotEmpty()) {
                            problems += "версии адаптеров не парны ядру $coreVersion: " +
                                mismatched.joinToString(", ") { "${it.name}:${it.version}" }
                        }
                    }
                }

                if (problems.isNotEmpty()) {
                    throw GradleException(
                        "Проверка рекламы не прошла (adsMode=$mode):\n" +
                            problems.joinToString("\n") { "  - $it" }
                    )
                }

                logger.lifecycle(
                    "verifyAdvertisingWiring: adsMode=$mode, адаптеров ${adapters.size}, " +
                        "ядро ${cores.firstOrNull()?.version ?: "нет"} — в порядке"
                )
            }
        }
    }
}

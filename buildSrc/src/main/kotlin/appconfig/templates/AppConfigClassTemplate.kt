package appconfig.templates

import appconfig.WhiteLabelConfiguration
import appconfig.GRADLE_TASK_NAME
import appconfig.ScreenToDisplay
import org.intellij.lang.annotations.Language

class AppConfigClassTemplate(
    private val whiteLabelConfig: WhiteLabelConfiguration
) {

    fun generateAppConfigClass(): String = appConfigClassString

    //private val enabledTabsList = buildEnabledTabsList(whiteLabelConfig)

    /*private fun buildEnabledTabsList(whiteLabelConfig: WhiteLabelConfiguration): String {
        val stringBuilder = StringBuilder()

        stringBuilder.append("listOf(")

        whiteLabelConfig.whiteLabelConfig.screensToDisplay.onEach { tab: ScreenToDisplay ->
            when (tab.type) {
                "flights" -> stringBuilder.append("AppTabs.Flights,\n")
                "hotels" -> stringBuilder.append("AppTabs.Hotels,\n")
                "other" -> {
                    if (tab.parameters != null) {
                        val id = tab.parameters.id.lowercase()
                        val tabText = """
                        AppTabs.Other(
                            idRes = R.id.$id,
                            graphId = R.navigation.$id,
                            title = R.string.title_$id,
                            icon = R.drawable.${tab.parameters.icon},
                            url = R.string.url_$id
                        ),
                    """
                        stringBuilder.append(tabText)
                    }
                }
                else -> return@onEach
            }
        }

        stringBuilder.append("AppTabs.Info")

        stringBuilder.append(")")

        return stringBuilder.toString()
    }*/

    private val enabledInfoItems = whiteLabelConfig.infoScreenConfig.itemsToDisplay.joinToString(
        separator = ", ",
    ) {
        "EnabledInfoItems.getByName(\"${it}\")"
    }

    @Language("kotlin")
    val appConfigClassString = """
        package com.travelapp.config
        
        import com.travelapp.sdk.config.CornerType
        import com.travelapp.sdk.config.AppTabs
        import com.travelapp.sdk.config.EnabledInfoItems
        import com.travelapp.sdk.config.IconsType
                
        /**
        * Do not modify! Auto generated by '$GRADLE_TASK_NAME' task
        */
        object WhiteLabelConf {
            
            const val apiKey: String = "${whiteLabelConfig.constants.apiKey.orEmpty()}"
        
            const val email: String = "${whiteLabelConfig.constants.feedbackEmail}"

            const val feedbackTheme: String = "${whiteLabelConfig.constants.feedbackTheme.orEmpty()}"

            const val appVerison: String = "${whiteLabelConfig.baseConfiguration.identifier.android.versionName}"

            const val appId: String = "${whiteLabelConfig.baseConfiguration.identifier.android.applicationId}"

            val reviewRequestFrequency: Int = ${whiteLabelConfig.baseConfiguration.reviewRequestFrequency ?: 0}

            val marker: Int = ${whiteLabelConfig.constants.marker}
            
            val clientDeviceHost: String = "${whiteLabelConfig.constants.clientDeviceHost}"
    
            val flightsTabEnabled: Boolean = ${whiteLabelConfig.whiteLabelConfig.screensToDisplay.any { it.type == "flights" }}
            
            val hotelsTabEnabled: Boolean = ${whiteLabelConfig.whiteLabelConfig.screensToDisplay.any { it.type == "hotels" }}

            const val sharingLink: String = "${whiteLabelConfig.constants.sharingData?.sharingLink.orEmpty()}"

            const val handlingLink: String = "${whiteLabelConfig.constants.sharingData?.handlingLink.orEmpty()}"

            const val appsflyerDevKey: String = "${whiteLabelConfig.constants.appsflyerDevKey.orEmpty()}"

            object Style {
                val cornerType: CornerType = CornerType.getByName("${whiteLabelConfig.style.cornersType}")
                val iconsType: IconsType = IconsType.getByName("${whiteLabelConfig.style.iconsType}")
            }
            
            object InfoConfig {
                val enabledItems: Set<EnabledInfoItems> = setOf<EnabledInfoItems?>(
                    $enabledInfoItems
                ).filterNotNull().toSet()
            }
        
            object Advertising {
                const val appodealApiKey: String = "${whiteLabelConfig.advertising?.appodealApiKey.orEmpty()}"

                object Placements {
                    const val airTicketPlacementInterstitial: String = "${whiteLabelConfig.advertising?.placements?.airTicketPlacementInterstitial.orEmpty()}"

                    const val airTicketPlacementBanner: String = "${whiteLabelConfig.advertising?.placements?.airTicketPlacementBanner.orEmpty()}"

                    const val hotelsPlacementInterstitial: String = "${whiteLabelConfig.advertising?.placements?.hotelsPlacementInterstitial.orEmpty()}"

                    const val hotelsPlacementBanner: String = "${whiteLabelConfig.advertising?.placements?.hotelsPlacementBanner.orEmpty()}"

                }
            }




            
            

            
        }
    """.trimIndent()

}
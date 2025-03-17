package com.travelapp.config

import com.travelapp.sdk.config.CornerType
import com.travelapp.sdk.config.AppTabs
import com.travelapp.sdk.config.EnabledInfoItems
import com.travelapp.sdk.config.IconsType
        
/**
* Do not modify! Auto generated by 'parseConfig' task
*/
object WhiteLabelConf {
    
    const val apiKey: String = ""

    const val email: String = "test@test.com"

    const val feedbackTheme: String = ""

    const val appVerison: String = "1.4.0"

    const val appId: String = ""

    val reviewRequestFrequency: Int = 0

    val marker: Int = 0
    
    val clientDeviceHost: String = ""

    val flightsTabEnabled: Boolean = true
    
    val hotelsTabEnabled: Boolean = true

    const val sharingLink: String = ""

    const val handlingLink: String = ""

    const val appsflyerDevKey: String = ""

    object Style {
        val cornerType: CornerType = CornerType.getByName("round")
        val iconsType: IconsType = IconsType.getByName("tint")
    }
    
    object InfoConfig {
        val enabledItems: Set<EnabledInfoItems> = setOf<EnabledInfoItems?>(
            EnabledInfoItems.getByName("about_app"), EnabledInfoItems.getByName("rate_app"), EnabledInfoItems.getByName("share_app"), EnabledInfoItems.getByName("favorites")
        ).filterNotNull().toSet()
    }

    object Advertising {
        const val appodealApiKey: String = ""

        object Placements {
            const val airTicketPlacementInterstitial: String = ""

            const val airTicketPlacementBanner: String = ""

            const val hotelsPlacementInterstitial: String = ""

            const val hotelsPlacementBanner: String = ""

        }
    }




    
    

    
}
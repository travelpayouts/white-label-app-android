package appconfig

import com.google.gson.annotations.SerializedName

class WhiteLabelConfiguration(

    @SerializedName("base_configuration")
    val baseConfiguration: BaseConfiguration,

    @SerializedName("constants")
    val constants: Constants,

    @SerializedName("style")
    val style: Style,

    @SerializedName("info_screen_config")
    val infoScreenConfig: InfoScreenConfig,

    @SerializedName("white_label_config")
    val whiteLabelConfig: WhiteLabelConfig

)

// region Base configuration

class BaseConfiguration(
    @SerializedName("identifier")
    val identifier: Identifier,

    @SerializedName("display_name")
    val displayName: Localization,

    @SerializedName("advertising")
    val advertising: Advertising?,
)

class Identifier(
    @SerializedName("android")
    val android: Platform
)

class Platform(
    @SerializedName("id")
    val applicationId: String,

    @SerializedName("versionName")
    val versionName: String,

    @SerializedName("versionCode")
    val versionCode: Int
)

class Advertising(
    @SerializedName("appodeal_api_key")
    val appodealApiKey: String,

    @SerializedName("google_admob_app_id")
    val googleAdmobAppId: String?,

    @SerializedName("placements")
    val placements: Placements,
)

class Placements(
    @SerializedName("air_ticket_placement_interstitial")
    val airTicketPlacementInterstitial: String,

    @SerializedName("air_ticket_placement_banner")
    val airTicketPlacementBanner: String,

    @SerializedName("hotels_placement_interstitial")
    val hotelsPlacementInterstitial: String,

    @SerializedName("hotels_placement_banner")
    val hotelsPlacementBanner: String,
)

// endregion

// region Constants

class Constants(
    @SerializedName("policy_url")
    val policyUrl: Localization,

    @SerializedName("feedback_email")
    val feedbackEmail: String?,

    @SerializedName("google_maps_api_key")
    val googleMapsApiKey: String,

    @SerializedName("marker")
    val marker: Int,

    @SerializedName("client_device_host")
    val clientDeviceHost: String,

    @SerializedName("host")
    val host: String,

    @SerializedName("api_key")
    val apiKey: String?,

    @SerializedName("sharing_data")
    val sharingData: SharingData?,

    @SerializedName("appsflyer_dev_key")
    val appsflyerDevKey: String?
)

class SharingData(
    @SerializedName("sharing_link")
    val sharingLink: String?,

    @SerializedName("handling_link")
    val handlingLink: String?
)

// endregion

// region Style

class Style(

    @SerializedName("base_color")
    val baseColor: String,

    @SerializedName("corners_type")
    val cornersType: String,

    @SerializedName("icons_type")
    val iconsType: String,

    @SerializedName("palette")
    val palette: String?,

    @SerializedName("overridden_color")
    val overriddenColor: Map<String, String>

)

// endregion

// region Info screen config

class InfoScreenConfig(

    @SerializedName("items_to_display")
    val itemsToDisplay: List<String>,

    @SerializedName("about_app_info")
    val aboutAppInfo: AboutAppInfo

)

class AboutAppInfo(

    @SerializedName("description")
    val description: Localization,

    @SerializedName("developer")
    val developer: Localization,

    @SerializedName("partner_url")
    val partnerUrl: Localization

)


// endregion

// region White label config

class WhiteLabelConfig(

    @SerializedName("screens_to_display")
    val screensToDisplay: List<ScreenToDisplay>

)

class ScreenToDisplay(
    @SerializedName("type")
    val type: String,

    @SerializedName("parameters")
    val parameters: TabParameters?
)

class TabParameters(

    @SerializedName("id")
    val id: String,

    @SerializedName("icon")
    val icon: String,

    @SerializedName("title")
    val title: Localization,

    @SerializedName("url")
    val url: Localization

)

// endregion

// region Common

class Localization(
    @SerializedName("base")
    val base: String,

    @SerializedName("localized")
    val localized: Map<String, String>
)

// endregion


package com.travelapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.travelapp.config.WhiteLabelConf
import com.travelapp.debugmenu.DebugMenu
import com.travelapp.sdk.config.AdvertisingConfig
import com.travelapp.sdk.config.CornerType
import com.travelapp.sdk.config.EnabledInfoItems
import com.travelapp.sdk.config.IconsType
import com.travelapp.sdk.config.SdkConfig
import com.travelapp.sdk.config.TravelSdk
import timber.log.Timber

open class TravelApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        initTimber()
        initTravelSdk()
    }

    private fun initTravelSdk() {
        val cornerType = CornerType.getByName(
            DebugMenu.getDebugValue(
                this,
                DebugMenu.SHAPE_KEY,
                WhiteLabelConf.Style.cornerType.name
            )
        )
        val icons = IconsType.getByName(
            DebugMenu.getDebugValue(
                this,
                DebugMenu.ICONS_KEY,
                WhiteLabelConf.Style.iconsType.name
            )
        )

        val advertisingConfig = if (DebugMenu.getShowAds(this)) {
            AdvertisingConfig(
                appodealApiKey = WhiteLabelConf.Advertising.appodealApiKey,
                airTicketPlacementInterstitial = WhiteLabelConf.Advertising.Placements.airTicketPlacementInterstitial,
                airTicketPlacementBanner = WhiteLabelConf.Advertising.Placements.airTicketPlacementBanner,
                hotelsPlacementInterstitial = WhiteLabelConf.Advertising.Placements.hotelsPlacementInterstitial,
                hotelsPlacementBanner = WhiteLabelConf.Advertising.Placements.hotelsPlacementBanner
            )
        } else {
            AdvertisingConfig()
        }

        val config = SdkConfig(
            icons = icons,
            cornerType = cornerType,
            marker = WhiteLabelConf.marker,
            apiKey = WhiteLabelConf.apiKey,
            flightsFavoritesEnabled = WhiteLabelConf.flightsTabEnabled,
            hotelsFavoritesEnabled = WhiteLabelConf.hotelsTabEnabled,
            flightsPriceSettingsEnabled = WhiteLabelConf.flightsTabEnabled,
            hotelsPriceSettingsEnabled = WhiteLabelConf.hotelsTabEnabled,
            enabledInfoItems = WhiteLabelConf.InfoConfig.enabledItems,
            clientDeviceHost = WhiteLabelConf.clientDeviceHost,
            appVersion = WhiteLabelConf.appVerison,
            email = WhiteLabelConf.email,
            advertising = advertisingConfig,
            sharingLink = WhiteLabelConf.sharingLink,
            handlingLink = WhiteLabelConf.handlingLink,
            appsflyerDevKey = WhiteLabelConf.appsflyerDevKey
        )

        TravelSdk.init(this, config)

    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun createNotificationChannel() {
        val name = "TravelApp"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(
            TravelAppMessagingService.NOTIFICATION_CHANNEL_ID,
            name,
            importance
        )
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

}
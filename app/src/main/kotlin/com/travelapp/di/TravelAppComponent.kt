/*
package com.travelapp.di

import com.travelapp.TravelApp
import com.travelapp.sdk.internal.core.di.CoreApi
import com.travelapp.sdk.internal.core.di.CoreComponent
import com.travelapp.LocaleChangedReceiver
import com.travelapp.sdk.internal.network.di.CoreNetworkApi
import com.travelapp.sdk.internal.network.di.CoreNetworkComponent
import com.travelapp.di.modules.NavigationTabModule
import com.travelapp.di.modules.TravelAppModule
import com.travelapp.ui.activities.MainActivity
import dagger.Component
import javax.inject.Singleton

@Component(
    modules = [
        TravelAppModule::class,
        NavigationTabModule::class
    ],
    dependencies = [TravelAppComponent.Dependencies::class]
)

@Singleton
internal interface TravelAppComponent {

    companion object {
        val instance: TravelAppComponent by lazy(LazyThreadSafetyMode.PUBLICATION) {
            val appDependencies = DaggerTravelAppComponent_Dependencies.builder()
                .coreApi(CoreComponent.get())
                .coreNetworkApi(CoreNetworkComponent.instance)
                .build()

            DaggerTravelAppComponent.builder()
                .dependencies(appDependencies)
                .build()
        }
    }

    @Component(
        dependencies = [
            CoreApi::class,
            CoreNetworkApi::class
        ]
    )
    interface Dependencies : TravelAppDependencies
}*/

package com.travelapp.config

import com.travelapp.sdk.config.AppTabs
import com.travelapp.sdk.R

object AppTabsList {

    fun get(): List<AppTabs> {
        return listOf<AppTabs>(
AppTabs.Flights,
AppTabs.Hotels,

                                AppTabs.Other(
                                    idRes = R.id.ta_other1,
                                    graphId = R.navigation.ta_other1,
                                    title = R.string.ta_title_other1,
                                    icon = R.drawable.ta_ic_other_1,
                                    url = R.string.ta_url_other1
                                ),
                            

                                AppTabs.Other(
                                    idRes = R.id.ta_other2,
                                    graphId = R.navigation.ta_other2,
                                    title = R.string.ta_title_other2,
                                    icon = R.drawable.ta_ic_other_2,
                                    url = R.string.ta_url_other2
                                ),
                            
AppTabs.Info,
)
    }
  

}
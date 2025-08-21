package appconfig.parsers

import appconfig.ScreenToDisplay
import org.gradle.api.Project
import org.intellij.lang.annotations.Language

object AppTabsParser {

    private const val packageNamePath = "/com/travelapp/config"

    private const val srcDir = "src/main/kotlin"

    private const val filename = "AppTabs.kt"

    /**
     * Generate AppTabs object with tabs to provide in MainActivity bottombar
     */
    fun parseTabs(appModule: Project, screensToDisplay: List<ScreenToDisplay>) {
        print("Generating AppTabs object...    ")

        val listString = buildString {
            append("listOf<AppTabs>(\n")

            var otherTabCount: Int = 0

            screensToDisplay.forEach { screen ->
                when (screen.type) {
                    "flights" -> {
                        append("AppTabs.Flights,\n")
                    }
                    "hotels" -> {
                        append("AppTabs.Hotels,\n")
                    }
                    "other" -> {
                        otherTabCount++
                        val id = "other$otherTabCount"

                        val params = screen.parameters ?: throw IllegalArgumentException("Parameters must not be null for 'other' tab")
                        LocalizationParser.parseLocalization(appModule, "ta_title_$id.xml", "ta_title_$id", params.title)
                        LocalizationParser.parseLocalization(appModule, "ta_url_$id.xml", "ta_url_$id", params.url)
                        append(
                            """
                                AppTabs.Other(
                                    idRes = R.id.ta_$id,
                                    graphId = R.navigation.ta_$id,
                                    title = R.string.ta_title_$id,
                                    icon = R.drawable.ta_ic_other_$otherTabCount,
                                    url = R.string.ta_url_$id
                                ),
                            """//.trimIndent()
                        )
                        append("\n")
                    }
                    else -> Unit
                }
            }

            append("AppTabs.Info,\n")

            append(")")
        }


        val file =
            appModule.layout.projectDirectory.file("$srcDir$packageNamePath/$filename").asFile
        file.parentFile.mkdirs()
        file.writeText(template.format(listString))

        println("✅ ")
    }


    @Language("kotlin")
    private val template = """
        package com.travelapp.config
        
        import com.travelapp.sdk.config.AppTabs
        import com.travelapp.sdk.R
       
        object AppTabsList {
        
            fun get(): List<AppTabs> {
                return %s
            }
          
        
        }
    """.trimIndent()

}
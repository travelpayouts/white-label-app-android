rootProject.name = "TravelApp"

include(":app")

include(":travel-sdk")
include(":common-debug-menu")

project(":common-debug-menu").projectDir = File("modules/common-debug-menu-noop")
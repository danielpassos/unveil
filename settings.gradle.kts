pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

includeBuild("build-logic")

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Unveil"
include(":sampleApp:composeApp")
include(":unveil-core")
include(":unveil-crash")
include(":unveil-deviceinfo")
include(":unveil-logs")
include(":unveil-logs-kermit")
include(":unveil-navigation")
include(":unveil-navigation-compose")
include(":unveil-network")
include(":unveil-network-ktor")

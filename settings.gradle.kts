pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Unveil"
include(":sampleApp:composeApp")
include(":unveil-core")
include(":unveil-network")
include(":unveil-network-ktor")

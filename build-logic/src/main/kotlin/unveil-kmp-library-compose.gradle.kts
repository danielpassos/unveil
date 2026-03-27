import com.android.build.api.dsl.androidLibrary
import org.gradle.api.artifacts.VersionCatalogsExtension

plugins {
    id("unveil-kmp-library")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
}

val catalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

kotlin {
    androidLibrary {
        androidResources.enable = true
    }
    sourceSets {
        commonMain.dependencies {
            implementation(catalog.findLibrary("compose-components-resources").get())
        }
    }
}

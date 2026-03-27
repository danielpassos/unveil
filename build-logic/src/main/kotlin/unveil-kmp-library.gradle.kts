import com.android.build.api.dsl.androidLibrary
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.kotlin.multiplatform.library")
}

// The type-safe `libs` accessor is not generated for precompiled script plugins
// in this Gradle setup. Access the catalog explicitly instead.
val catalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

kotlin {
    androidLibrary {
        compileSdk = catalog.findVersion("android-compileSdk").get().requiredVersion.toInt()
        minSdk = catalog.findVersion("android-minSdk").get().requiredVersion.toInt()

        compilations.configureEach {
            compilerOptions.configure {
                jvmTarget.set(JvmTarget.JVM_21)
            }
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonTest.dependencies {
            // kotlin("test") resolves automatically from the applied Kotlin plugin version
            implementation(kotlin("test"))
            implementation(catalog.findLibrary("kotlinx-coroutines-test").get())
        }
    }
}

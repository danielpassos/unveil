import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.vanniktech.mavenPublish)
}

group = "io.github.kotlin"
version = "1.0.0"

kotlin {
    androidLibrary {
        namespace = "org.jetbrains.kotlinx.multiplatform.library.template"
        compileSdk =
            libs.versions.android.compileSdk
                .get()
                .toInt()
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()

        compilations.configureEach {
            compilerOptions.configure {
                jvmTarget.set(
                    JvmTarget.JVM_11,
                )
            }
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            // put your multiplatform dependencies here
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

mavenPublishing {
    publishToMavenCentral()

    signAllPublications()

    coordinates(group.toString(), "library", version.toString())

    pom {
        name = "Unveil"
        description =
            "Unveil is a developer panel for Kotlin Multiplatform apps. Swipe right to left, and see everything your app is doing under the hood, network requests, logs, feature flags, navigation state, and more. Disable it with one line. Your users will never know it's there."
        inceptionYear = "2024"
        url = "https://github.com/danielpassos/unveil/"
        licenses {
            licenses {
                license {
                    name = "Apache License, Version 2.0"
                    url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                    distribution = "repo"
                }
            }
        }
        developers {
            developer {
                id = "danielpassos"
                name = "Daniel Passos"
                url = "https://daniel.passos.me"
            }
        }
        scm {
            url = "https://github.com/danielpassos/unveil/"
            connection = "scm:git:git://github.com/danielpassos/unveil.git"
            developerConnection = "scm:git:ssh://git@github.com:danielpassos/unveil.git"
        }
    }
}

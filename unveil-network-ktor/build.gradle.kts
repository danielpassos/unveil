import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.vanniktech.mavenPublish)
}

group = "me.passos.libs.unveil"
version = "0.0.1"

kotlin {
    androidLibrary {
        namespace = "me.passos.libs.unveil.network.ktor"
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
                jvmTarget.set(JvmTarget.JVM_21)
            }
        }
    }
    if (System.getenv("CI") == "true") {
        linuxX64()
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":unveil-network"))
            implementation(libs.ktor.client.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.ktor.client.mock)
        }
    }
}

mavenPublishing {
    publishToMavenCentral()

    coordinates(group.toString(), "unveil-network-ktor", version.toString())

    pom {
        name = "Unveil Network — Ktor"
        description = "Ktor adapter for the Unveil Network plugin."
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
